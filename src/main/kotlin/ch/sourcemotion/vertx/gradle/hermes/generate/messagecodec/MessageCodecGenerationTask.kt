package ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec

import ch.sourcemotion.vertx.gradle.hermes.files.toPath
import ch.sourcemotion.vertx.gradle.hermes.generate.*
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGeneratorTaskInternalOutput
import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesExtension
import ch.sourcemotion.vertx.gradle.hermes.plugin.task.AbstractHermesTask
import org.codehaus.groovy.reflection.ClassInfo
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.util.function.Function

abstract class MessageCodecGenerationTask : AbstractHermesTask(), DefinesPackageName, DefinesOutputDir,
    DefinesClassesInfo, DefinesMessageCodecsFileName, DefinesMessageCodecNameSupplier {

    companion object {
        const val NAME = "generateMessageCodec"
        internal const val DEFAULT_MESSAGE_CODEC_FILE_NAME = "HermesMessageCodecs"
        internal val DEFAULT_MESSAGE_CODEC_NAME_SUPPLIER =
            Function<DtoClassInfo, String> { "${it.className}MessageCodec" }
    }

    @get:Input
    abstract override val packageName: Property<String>

    @get:Input
    abstract override val messageCodecsFileName: Property<String>

    @get:Internal
    abstract override val messageCodecNameSupplier: Property<Function<DtoClassInfo, String>>

    @get:Input
    abstract override val classesInfo: ListProperty<DtoClassInfo>

    @get:Internal
    internal abstract val generatedClassesInfo: Property<DtoGeneratorTaskInternalOutput>

    @get:OutputDirectory
    abstract override val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val configuration = createGeneratorConfiguration()
        try {
            MessageCodecGenerator().generate(configuration)
        } catch (e: Exception) {
            throw MessageCodecGeneratorException(
                "Failed to generate message codec according configuration $configuration", e
            )
        }
    }

    /**
     * We have to evaluate the classes info about the message codec classes recently before task run because otherwise
     * we are too early that the extension, task is configured by script(s) or the too late.
     */
    private fun evaluateClassesInfo() : List<DtoClassInfo> {
        val fromExtension = project.extensions.getByType(HermesExtension::class.java).codec.classesInfo.getOrElse(
            emptyList())
        val fromDtoGenTask = generatedClassesInfo.get().generatedClasses.getOrElse(emptyList())
        val fromThisTask = classesInfo.getOrElse(emptyList())
        return (fromExtension + fromDtoGenTask + fromThisTask).distinct()
    }

    internal fun createDefaultRelativeOutputPath(sourceSet: SourceSet) =
        "generated/sources/hermes/kotlin/${sourceSet.name}".toPath()

    private fun createGeneratorConfiguration(): MessageCodecGeneratorConfiguration {
        val classesInfo = evaluateClassesInfo()
        return MessageCodecGeneratorConfiguration(
            outputDir.asFile.get(),
            packageName.get(),
            messageCodecsFileName.get(),
            classesInfo,
            messageCodecNameSupplier.get()
        )
    }
}