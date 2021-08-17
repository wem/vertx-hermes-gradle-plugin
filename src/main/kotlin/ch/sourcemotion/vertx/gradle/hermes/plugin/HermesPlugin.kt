package ch.sourcemotion.vertx.gradle.hermes.plugin

import ch.sourcemotion.vertx.gradle.hermes.generate.communication.CommunicationGenerationTask
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGenerationTask
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGeneratorTaskInternalOutput
import ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec.MessageCodecGenerationTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
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
        val codecGenTaskProvider = project.registerCodecGenTask()
        val commGenTaskProvider = project.registerCommunicationGenTask()

        val outputPaths = mutableSetOf<Path>()

        project.afterEvaluate {
            val sourceSet = extension.evaluateSourceSet()
            val projectInputPath = project.projectDir.toPath()
            val projectOutputPath = project.buildDir.toPath()

            val dtoGenTaskEnabled = try {
                configureDtoGenTask(
                    dtoGenTaskProvider,
                    extension,
                    dtoGeneratorTaskInternalOutput,
                    projectInputPath,
                    sourceSet,
                    projectOutputPath,
                    outputPaths
                )
            } catch (e: Exception) {
                throw if (e is GradleException) {
                    e
                } else GradleException("Failed to configure dto generation task", e)
            }

            try {
                configureMessageCodecGenTask(
                    dtoGenTaskProvider,
                    extension,
                    codecGenTaskProvider,
                    project,
                    dtoGeneratorTaskInternalOutput,
                    projectOutputPath,
                    sourceSet,
                    outputPaths,
                    dtoGenTaskEnabled
                )
            } catch (e: Exception) {
                throw if (e is GradleException) {
                    e
                } else GradleException("Failed to configure message codec generation task", e)
            }

            try {
                configureCommGenTask(
                    commGenTaskProvider,
                    extension,
                    projectInputPath,
                    sourceSet,
                    projectOutputPath,
                    outputPaths,
                    dtoGenTaskEnabled,
                    dtoGenTaskProvider
                )
            } catch (e: Exception) {
                throw if (e is GradleException) {
                    e
                } else GradleException("Failed to configure communication generation task", e)
            }

            outputPaths.forEach { sourceSet.allJava.srcDir("$it") }
        }
    }

    private fun configureCommGenTask(
        commGenTaskProvider: TaskProvider<CommunicationGenerationTask>,
        extension: HermesExtension,
        projectInputPath: Path,
        sourceSet: SourceSet,
        projectOutputPath: Path,
        outputPaths: MutableSet<Path>,
        dtoGenTaskEnabled: Boolean,
        dtoGenTaskProvider: TaskProvider<DtoGenerationTask>
    ) {
        val commGenTaskEnabled = commGenTaskProvider.isTaskEnabled(extension.communication.enable)
        if (commGenTaskEnabled) {
            val task = commGenTaskProvider.get()
            task.applyFinalGenerateSender(extension.communication)
            task.applyFinalGenerateConsumer(extension.communication)
            task.applyFinalPackageName(extension.communication)
            task.applyFinalInputPath(
                projectInputPath,
                extension.communication,
                task.createDefaultRelativeInputPath(sourceSet)
            )
            val finalOutputPath = task.applyFinalOutputPath(
                projectOutputPath,
                extension.communication,
                task.createDefaultRelativeOutputPath(sourceSet)
            )
            outputPaths.add(finalOutputPath)

            if (dtoGenTaskEnabled) {
                task.shouldRunAfter(dtoGenTaskProvider.get())
            }
        }
    }

    private fun configureMessageCodecGenTask(
        dtoGenTaskProvider: TaskProvider<DtoGenerationTask>,
        extension: HermesExtension,
        codecGenTask: TaskProvider<MessageCodecGenerationTask>,
        project: Project,
        dtoGeneratorTaskInternalOutput: Provider<DtoGeneratorTaskInternalOutput>,
        projectOutputPath: Path,
        sourceSet: SourceSet,
        outputPaths: MutableSet<Path>,
        dtoGenTaskEnabled: Boolean
    ) {
        val taskEnabled = codecGenTask.isTaskEnabled(extension.codec.enable)
        codecGenTask.get().enabled = taskEnabled
        if (taskEnabled) {
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
    }

    private fun configureDtoGenTask(
        dtoGenTaskProvider: TaskProvider<DtoGenerationTask>,
        extension: HermesExtension,
        dtoGeneratorTaskInternalOutput: Provider<DtoGeneratorTaskInternalOutput>,
        projectInputPath: Path,
        sourceSet: SourceSet,
        projectOutputPath: Path,
        outputPaths: MutableSet<Path>
    ): Boolean {
        val taskEnabled = dtoGenTaskProvider.isTaskEnabled(extension.dto.enable)
        dtoGenTaskProvider.get().enabled = taskEnabled
        if (taskEnabled) {
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
        return taskEnabled
    }

    private fun Project.registerCodecGenTask() =
        project.tasks.register(MessageCodecGenerationTask.NAME, MessageCodecGenerationTask::class.java)

    private fun Project.registerDtoGenTask(): TaskProvider<DtoGenerationTask> =
        project.tasks.register(DtoGenerationTask.NAME, DtoGenerationTask::class.java)

    private fun Project.registerCommunicationGenTask(): TaskProvider<CommunicationGenerationTask> =
        project.tasks.register(CommunicationGenerationTask.NAME, CommunicationGenerationTask::class.java)

    private fun TaskProvider<out Task>.isTaskEnabled(extensionFlag: Property<Boolean>) =
        extensionFlag.get() && get().enabled
}