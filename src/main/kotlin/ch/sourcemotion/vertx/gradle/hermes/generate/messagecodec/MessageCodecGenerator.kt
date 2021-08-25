package ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec

import ch.sourcemotion.vertx.gradle.hermes.files.add
import ch.sourcemotion.vertx.gradle.hermes.files.addPackage
import ch.sourcemotion.vertx.gradle.hermes.files.toPath
import ch.sourcemotion.vertx.gradle.hermes.generate.Generator
import ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonObject
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.function.Function

class MessageCodecGenerator : Generator<MessageCodecGeneratorConfiguration, Path>() {

    private companion object {
        val messageCodecInterfaceClassName: String = MessageCodec::class.java.simpleName
        val messageCodecPackageName: String = MessageCodec::class.java.packageName

        val jsonObjectClassName: String = JsonObject::class.java.simpleName
        val jsonObjectPackageName: String = JsonObject::class.java.packageName

        val bufferClassName: String = Buffer::class.java.simpleName
        val bufferPackageName: String = Buffer::class.java.packageName

        const val JSON_BASE_MESSAGE_CODEC_CLASS_NAME = "BaseJsonMessageCodec"
    }

    override fun generate(configuration: MessageCodecGeneratorConfiguration): Path {
        val fileSpecBuilder = FileSpec.builder(configuration.packageName, configuration.codecsFileName)
        configuration.classInfos.forEach {
            fileSpecBuilder.generateMessageCodec(configuration, it, configuration.messageCodecNameSupplier)
        }
        fileSpecBuilder.addImports().build().writeTo(configuration.outputDir)

        val outputFilePath = configuration.outputDir.toPath()
            .addPackage(configuration.packageName)
            .add("${configuration.codecsFileName}.kt".toPath())

        FileOutputStream(outputFilePath.toFile(), true).use { fos ->
            fos.bufferedWriter().use {
                it.writeBaseMessageCodecSource()
            }
        }
        return outputFilePath
    }

    private fun FileSpec.Builder.generateMessageCodec(
        configuration: MessageCodecGeneratorConfiguration,
        classInfo: DtoClassInfo,
        messageCodecNameSupplier: Function<DtoClassInfo, String>
    ) {
        val dtoClassName = ClassName(classInfo.packageName, classInfo.className)
        val baseJsonMessageCodecTypeName = ClassName(configuration.packageName, JSON_BASE_MESSAGE_CODEC_CLASS_NAME)
            .parameterizedBy(dtoClassName)

        addType(
            TypeSpec.classBuilder("${classInfo.className}MessageCodec")
                .superclass(baseJsonMessageCodecTypeName)
                .addSuperclassConstructorParameter("%T::class.java", dtoClassName)
                .addSuperclassConstructorParameter("%S", messageCodecNameSupplier.apply(classInfo))
                .build()
        )
    }

    private fun BufferedWriter.writeBaseMessageCodecSource() {
        val baseMessageCodecSourceStream =
            MessageCodecGenerator::class.java.getResourceAsStream("/kotlin-source/base-json-message-codec")
                ?: throw MessageCodecGeneratorException("Source for base json message codec not found. This leads to a plugin bug. Please report this issue")
        val source = baseMessageCodecSourceStream.use { fis -> fis.bufferedReader().use { it.readText() } }
        newLine()
        newLine()
        write(source)
    }


    private fun FileSpec.Builder.addImports(): FileSpec.Builder {
        addImport(messageCodecPackageName, messageCodecInterfaceClassName)
        addImport(jsonObjectPackageName, jsonObjectClassName)
        addImport(bufferPackageName, bufferClassName)
        return this
    }
}