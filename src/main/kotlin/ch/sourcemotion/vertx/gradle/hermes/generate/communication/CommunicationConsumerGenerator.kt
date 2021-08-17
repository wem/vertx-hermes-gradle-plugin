package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.generate.Generator
import ch.sourcemotion.vertx.gradle.hermes.generate.communication.CommunicationGenerator.Companion.unitTypeName
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class CommunicationConsumerGenerator : CommunicationGenerator, Generator<CommunicationGeneratorConfiguration, Unit>() {

    companion object {
        internal const val OUTPUT_FILE_NAME = "HermesCommunicationConsumer"
        private const val CONSUMER_BLOCK_PARAM_NAME = "block"
    }

    override fun generate(configuration: CommunicationGeneratorConfiguration) {
        val definitionFilePaths = findAllCommunicationDefinitionFiles(configuration.inputDir.absolutePath)
        val definitions = definitionFilePaths.validateAndParse()

        val fileSpecBuilder = FileSpec.builder(configuration.packageName, OUTPUT_FILE_NAME)
        definitions.forEach { fileSpecBuilder.generateCommunicationConsumer(it) }
        fileSpecBuilder.build().writeTo(configuration.outputDir)
    }

    private fun List<File>.validateAndParse(): List<CommunicationDefinition> {
        val fileContent = map { it.readText() }
        fileContent.forEach { CommunicationJsonValidator.validate(it) }
        return fileContent.flatMap { CommunicationParser.parseDefinitions(it) }
    }

    private fun findAllCommunicationDefinitionFiles(basePathValue: String): List<File> {
        val basePath = Paths.get(basePathValue)
        if (Files.isRegularFile(basePath)) {
            return listOf(basePath.toFile())
        }

        return Files.list(basePath).toList().flatMap {
            val file = it.toFile()
            if (file.isDirectory) {
                findAllCommunicationDefinitionFiles("$it")
            } else {
                listOf(file)
            }
        }
    }


    private fun FileSpec.Builder.generateCommunicationConsumer(definition: CommunicationDefinition) {
        val funSpecBuilder = FunSpec.builder(definition.createFunctionName())
        funSpecBuilder.receiver(EventBus::class)
        val sendMessageType = evaluateTypeName(definition.sendMessageType, unitTypeName)
        val replyMessageType = evaluateTypeName(definition.replyMessageType, unitTypeName)
        funSpecBuilder.withStatement(definition, sendMessageType, replyMessageType)
        funSpecBuilder.withParameters(sendMessageType, replyMessageType)

        addFunction(funSpecBuilder.build())
    }

    private fun FunSpec.Builder.withStatement(
        definition: CommunicationDefinition,
        sendMessageType: TypeName,
        replyMessageType: TypeName
    ) {
        val params = listOf(sendMessageType, definition.address)
        val consumerFunction = if (definition.localOnly) {
            "localConsumer"
        } else "consumer"
        if (definition.sendType == SendType.REQUEST) {
            addStatement("$consumerFunction<%T>(%S) {", *params.toTypedArray())
            if (replyMessageType == unitTypeName) {
                addStatement("it.$CONSUMER_BLOCK_PARAM_NAME()")
                addStatement("it.reply(null) ")
            } else {
                addStatement("val result = it.$CONSUMER_BLOCK_PARAM_NAME()")
                addStatement("it.reply(result) ")
            }
            addStatement("}")
        } else {
            addStatement("consumer<%T>(%S, %L)", *(params + listOf(CONSUMER_BLOCK_PARAM_NAME)).toTypedArray())
        }
    }

    private fun FunSpec.Builder.withParameters(sendMessageType: TypeName, replyMessageType: TypeName) {
        val receiver = Message::class.java.asClassName().parameterizedBy(sendMessageType)
        val consumerBlockParamType = LambdaTypeName.get(receiver = receiver, returnType = replyMessageType)
        val consumerBlockParam = ParameterSpec.builder(CONSUMER_BLOCK_PARAM_NAME, consumerBlockParamType).build()
        addParameter(consumerBlockParam)
    }

    private fun CommunicationDefinition.createFunctionName() = "${name.decapitalize()}Consumer"
}

