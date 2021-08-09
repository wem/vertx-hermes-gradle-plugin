package ch.sourcemotion.vertx.gradle.hermes.generate.dto

import ch.sourcemotion.vertx.gradle.hermes.files.toPath
import ch.sourcemotion.vertx.gradle.hermes.generate.DefinesInputDir
import ch.sourcemotion.vertx.gradle.hermes.generate.DefinesOutputDir
import ch.sourcemotion.vertx.gradle.hermes.generate.DefinesPackageName
import ch.sourcemotion.vertx.gradle.hermes.plugin.task.AbstractHermesTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class DtoGenerationTask : AbstractHermesTask(), DefinesPackageName, DefinesInputDir, DefinesOutputDir {

    companion object {
        const val NAME = "generateDto"
    }

    @get:Input
    abstract override val packageName: Property<String>

    @get:[InputDirectory SkipWhenEmpty]
    abstract override val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract override val outputDir: DirectoryProperty

    @get:Internal
    internal abstract val generatedClassInfo: Property<DtoGeneratorTaskInternalOutput>

    @TaskAction
    fun generate() {
        val configuration = createGeneratorConfiguration()
        val generatedClasses = DtoGenerator().generate(configuration)
        generatedClassInfo.get().generatedClasses.set(generatedClasses)
    }

    internal fun createDefaultRelativeInputPath(sourceSet: SourceSet) = "src/${sourceSet.name}/hermes/dto".toPath()

    internal fun createDefaultRelativeOutputPath(sourceSet: SourceSet) =
        "generated/sources/hermes/kotlin/${sourceSet.name}".toPath()

    private fun createGeneratorConfiguration() =
        DtoGeneratorConfiguration(inputDir.get().asFile, outputDir.get().asFile, packageName.get())
}