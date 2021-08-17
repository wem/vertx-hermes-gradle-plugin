package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.generate.Generator
import ch.sourcemotion.vertx.gradle.hermes.generate.communication.CommunicationGenerator.Companion.unitTypeName
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.vertx.core.Future
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class CommunicationSenderGenerator : CommunicationGenerator, Generator<CommunicationGeneratorConfiguration, Unit>() {

    companion object {
        internal const val OUTPUT_FILE_NAME = "HermesCommunicationSend"
        private const val MESSAGE_PARAM_NAME = "message"

        private val basicImports = mapOf(
            "io.vertx.kotlin.coroutines" to "await",
            "io.vertx.kotlin.core.eventbus" to "deliveryOptionsOf"
        )
    }

    override fun generate(configuration: CommunicationGeneratorConfiguration) {
        val definitionFilePaths = findAllCommunicationDefinitionPaths(configuration.inputDir.absolutePath)
        val definitions = definitionFilePaths.validateAndParse()

        val fileSpecBuilder = FileSpec.builder(configuration.packageName, OUTPUT_FILE_NAME)
        definitions.forEach { fileSpecBuilder.generateCommunicationSender(it) }
        fileSpecBuilder.withImports()
        fileSpecBuilder.build().writeTo(configuration.outputDir)
    }

    private fun FileSpec.Builder.withImports() {
        basicImports.forEach { import -> addImport(import.key, import.value) }
    }

    private fun List<File>.validateAndParse(): List<CommunicationDefinition> {
        val fileContent = map { it.readText() }
        fileContent.forEach { CommunicationJsonValidator.validate(it) }
        return fileContent.flatMap { CommunicationParser.parseDefinitions(it) }
    }

    private fun findAllCommunicationDefinitionPaths(basePathValue: String): List<File> =
        Files.list(Paths.get(basePathValue)).toList().flatMap {
            val file = it.toFile()
            if (file.isDirectory) {
                findAllCommunicationDefinitionPaths("$it")
            } else {
                listOf(file)
            }
        }

    private fun FileSpec.Builder.generateCommunicationSender(definition: CommunicationDefinition): List<TypeName> {
        val funSpecBuilder = FunSpec.builder(definition.createFunctionName())
        val additionImports = ArrayList<TypeName>()
        funSpecBuilder.receiver(EventBus::class)
        val returnType = evaluateTypeName(definition.replyMessageType, unitTypeName)
        val sendType = funSpecBuilder.withSendType(definition)?.also { additionImports.add(it) }
        funSpecBuilder.withStatementAndModifier(definition, sendType, returnType)

        addFunction(funSpecBuilder.build())

        return additionImports
    }

    private fun FunSpec.Builder.withStatementAndModifier(
        definition: CommunicationDefinition,
        sendMessageType: ClassName?,
        replyMessageType: TypeName
    ) {
        val messageParam = if (sendMessageType != null) {
            MESSAGE_PARAM_NAME
        } else "null"

        val params = listOf(definition.address, messageParam, definition.createDeliveryOptions())

        when (definition.sendType) {
            SendType.PUBLISH -> {
                addStatement("publish(%S, %L, %L)", *params.toTypedArray())
            }
            SendType.SEND -> {
                addStatement("send(%S, %L, %L)", *params.toTypedArray())
            }
            SendType.REQUEST -> {
                returns(Message::class.java.asClassName().parameterizedBy(replyMessageType))
                val requestParams = listOf(replyMessageType) + params
                addModifiers(KModifier.SUSPEND)
                addStatement("return request<%T>(%S, %L, %L).await()", *requestParams.toTypedArray())
            }
        }
    }

    private fun FunSpec.Builder.withSendType(definition: CommunicationDefinition): ClassName? =
        if (definition.sendMessageType != null) {
            val sendMessageType = ClassName.bestGuess(definition.sendMessageType)
            val param = ParameterSpec.builder(MESSAGE_PARAM_NAME, sendMessageType).build()
            sendMessageType.also { addParameter(param) }
        } else null

    private fun CommunicationDefinition.createFunctionName() =
        if (sendType == SendType.SEND && replyMessageType == null) {
            "send${name}"
        } else if (sendType == SendType.REQUEST) {
            "request${name}"
        } else "publish${name}"

    private fun CommunicationDefinition.createDeliveryOptions() = "deliveryOptionsOf(localOnly = $localOnly)"
}

