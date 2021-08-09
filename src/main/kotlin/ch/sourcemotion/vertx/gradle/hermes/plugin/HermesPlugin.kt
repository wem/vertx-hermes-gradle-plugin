package ch.sourcemotion.vertx.gradle.hermes.plugin

import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGenerationTask
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGeneratorTaskInternalOutput
import ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec.MessageCodecGenerationTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import java.nio.file.Path

class HermesPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("hermes", HermesExtension::class.java).applyFixDefaultValues()

        val dtoGeneratorTaskInternalOutput = project.gradle.sharedServices.registerIfAbsent(
            "DtoGeneratorTaskOutput",
            DtoGeneratorTaskInternalOutput::class.java
        ) {}

        val dtoGenTaskProvider = project.registerDtoGenTask()
        val codecGenTask = project.registerCodecGenTask()

        val outputPaths = mutableSetOf<Path>()

        project.afterEvaluate {
            val sourceSet = extension.evaluateSourceSet()
            val projectInputPath = project.projectDir.toPath()
            val projectOutputPath = project.buildDir.toPath()

            val dtoGenTaskEnabled = dtoGenTaskProvider.isTaskEnabled(extension.dto.enable)
            dtoGenTaskProvider.get().enabled = dtoGenTaskEnabled
            if (dtoGenTaskEnabled) {
                val task = dtoGenTaskProvider.get()
                task.generatedClassInfo.set(dtoGeneratorTaskInternalOutput)
                task.applyFinalInputPath(
                    projectInputPath,
                    extension.dto,
                    task.createDefaultRelativeInputPath(sourceSet)
                )
                task.applyFinalPackageName(extension.dto)

                val finalOutputPath = task.applyFinalOutputPath(
                    projectOutputPath,
                    extension.dto,
                    task.createDefaultRelativeOutputPath(sourceSet)
                )
                outputPaths.add(finalOutputPath)
            }

            val codecGenTaskEnabled = dtoGenTaskProvider.isTaskEnabled(extension.codec.enable)
            codecGenTask.get().enabled = codecGenTaskEnabled
            if (codecGenTaskEnabled) {
                val task = codecGenTask.get()
                task.applyFinalPackageName(extension.codec)
                task.applyFinalClassesInfo(project, extension.codec, dtoGeneratorTaskInternalOutput)
                task.applyFinalMessageCodecsFileName(extension.codec)
                task.applyFinalDefinesMessageCodecNameSupplier(extension.codec)

                val finalOutputPath = task.applyFinalOutputPath(
                    projectOutputPath,
                    extension.codec,
                    task.createDefaultRelativeOutputPath(sourceSet)
                )
                outputPaths.add(finalOutputPath)

                if (dtoGenTaskEnabled) {
                    task.shouldRunAfter(dtoGenTaskProvider.get())
                }
            }

            outputPaths.forEach { sourceSet.allJava.srcDir("$it") }
        }
    }

    private fun Project.registerCodecGenTask() =
        project.tasks.register(MessageCodecGenerationTask.NAME, MessageCodecGenerationTask::class.java)

    private fun Project.registerDtoGenTask(): TaskProvider<DtoGenerationTask> =
        project.tasks.register(DtoGenerationTask.NAME, DtoGenerationTask::class.java)

    private fun TaskProvider<out Task>.isTaskEnabled(extensionFlag: Property<Boolean>) =
        extensionFlag.get() && get().enabled
}