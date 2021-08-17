package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.files.toPath
import ch.sourcemotion.vertx.gradle.hermes.generate.*
import ch.sourcemotion.vertx.gradle.hermes.plugin.task.AbstractHermesTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class CommunicationGenerationTask : AbstractHermesTask(), DefinesPackageName, DefinesInputDir,
    DefinesOutputDir, DefinesGenerateSender, DefinesGenerateConsumer {

    companion object {
        const val NAME = "generateCommunication"
    }

    @get:Input
    abstract override val packageName: Property<String>

    @get:[InputDirectory SkipWhenEmpty]
    abstract override val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract override val outputDir: DirectoryProperty

    @get:Input
    abstract override val generateSender: Property<Boolean>

    @get:Input
    abstract override val generateConsumer: Property<Boolean>

    @TaskAction
    fun generate() {
        val configuration = createGeneratorConfiguration()
        if (generateConsumer.get()) {
            try {
                CommunicationConsumerGenerator().generate(configuration)
            } catch (e: Exception) {
                throw if (e is GradleException) e else GradleException("Failed to generate communication consumer ${e::class.java.name} : ${e.message}", e)
            }
        }
        if (generateSender.get()) {
            try {
                CommunicationSenderGenerator().generate(configuration)
            } catch (e: Exception) {
                throw if (e is GradleException) e else GradleException("Failed to generate communication sender", e)
            }
        }
    }


    internal fun createDefaultRelativeInputPath(sourceSet: SourceSet) =
        "src/${sourceSet.name}/hermes/communication".toPath()

    internal fun createDefaultRelativeOutputPath(sourceSet: SourceSet) =
        "generated/sources/hermes/kotlin/${sourceSet.name}".toPath()

    private fun createGeneratorConfiguration() =
        CommunicationGeneratorConfiguration(inputDir.get().asFile, outputDir.get().asFile, packageName.get())
}