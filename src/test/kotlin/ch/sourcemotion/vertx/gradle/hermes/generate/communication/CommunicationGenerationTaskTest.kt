package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.*
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGenerationTask
import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesExtension
import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesPlugin
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Paths

internal class CommunicationGenerationTaskTest : AbstractProjectSupportTest(), DefaultTaskTest, DefinesInputDirImplTest,
    DefinesOutputDirImplTest, DefinesPackageNameImplTest, DefinesGenerateSenderImplTest,
    DefinesGenerateConsumerImplTest {

    @Test
    override fun with_default_values() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        project.evaluate()

        val sourceSet = project.sourceSetToVerify()
        val task = project.taskInstance<CommunicationGenerationTask>()
        task.inputDir.get()
            .shouldBe(project.inputDirectoryPropertyOf(task.createDefaultRelativeInputPath(sourceSet)).get())
        task.outputDir.get()
            .shouldBe(project.outputDirectoryPropertyOf(task.createDefaultRelativeOutputPath(sourceSet)).get())
        task.packageName.isPresent.shouldBeFalse()
        task.generateSender.isPresent.shouldBeTrue()
        task.generateSender.get().shouldBeTrue()
        task.generateConsumer.isPresent.shouldBeTrue()
        task.generateConsumer.get().shouldBeTrue()
    }

    @Test
    override fun disabled() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        project.tasks.withType(CommunicationGenerationTask::class.java) {
            it.enabled = false
        }

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()

        task.inputDir.isPresent.shouldBeFalse()
        task.outputDir.isPresent.shouldBeFalse()
        task.packageName.isPresent.shouldBeFalse()
        task.generateSender.isPresent.shouldBeFalse()
        task.generateConsumer.isPresent.shouldBeFalse()
    }

    @Test
    override fun execute_task(@TempDir tempDir: File) {
        val projectSourceBase =
            CommunicationGenerationTaskTest::class.java.getResource("/fixture/communication/task")
                ?: throw IllegalStateException("Communication gen task execution test fixtures not found")
        val projectDir = File(tempDir, "project")
        copyFolder(Paths.get(projectSourceBase.toURI()), projectDir.toPath())
        executeBuildAndVerify(projectDir, CommunicationGenerationTask.NAME, TaskOutcome.SUCCESS)
        executeBuildAndVerify(projectDir, CommunicationGenerationTask.NAME, TaskOutcome.UP_TO_DATE)
    }

    @Test
    override fun with_alternative_input_dir_on_task() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        val expectedInputDir = File("${project.projectDir}/some-other-src-dir")
        project.tasks.withType(CommunicationGenerationTask::class.java) {
            it.inputDir.set(project.objects.directoryProperty().apply { set(File("some-other-src-dir")) })
        }

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()
        task.inputDir.get().asFile.shouldBe(expectedInputDir)
    }

    @Test
    override fun with_alternative_input_dir_on_extension() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        val expectedInputDir = File("${project.projectDir}/some-other-src-dir")
        project.extensions.getByType(HermesExtension::class.java).communication.inputDir.set(
            project.objects.directoryProperty().apply { set(File("some-other-src-dir")) })

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()
        task.inputDir.get().asFile.shouldBe(expectedInputDir)
    }

    @Test
    override fun with_package_name_on_task() {
        val packageName = "ch.sourcemotion"

        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        project.tasks.withType(CommunicationGenerationTask::class.java) {
            it.packageName.set(packageName)
        }

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()
        task.packageName.get().shouldBe(packageName)
    }

    @Test
    override fun with_package_name_on_extension() {
        val packageName = "ch.sourcemotion"

        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        project.extensions.getByType(HermesExtension::class.java).communication.packageName.set(packageName)

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()
        task.packageName.get().shouldBe(packageName)
    }

    @Test
    override fun with_alternative_output_dir_on_task() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        val expectedOutputDir = File("${project.buildDir}/some-other-build-dir")
        project.tasks.withType(CommunicationGenerationTask::class.java) {
            it.outputDir.set(
                project.objects.directoryProperty().apply { set(File("${project.buildDir}/some-other-build-dir")) })
        }

        project.evaluate()

        val sourceSet = project.sourceSetToVerify()
        val task = project.taskInstance<CommunicationGenerationTask>()
        task.outputDir.get().asFile.shouldBe(expectedOutputDir)
        sourceSet.verifySourceDir(expectedOutputDir.toPath())
    }

    @Test
    override fun with_alternative_output_dir_on_extension() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        val expectedOutputDir = File("${project.buildDir}/some-other-build-dir")
        project.extensions.getByType(HermesExtension::class.java).dto.outputDir.set(
            project.objects.directoryProperty().apply { set(File("${project.buildDir}/some-other-build-dir")) })

        project.evaluate()

        val sourceSet = project.sourceSetToVerify()
        val task = project.tasks.withType(DtoGenerationTask::class.java).shouldHaveSize(1).first()
        task.outputDir.get().asFile.shouldBe(expectedOutputDir)
        sourceSet.verifySourceDir(expectedOutputDir.toPath())
    }

    @Test
    override fun with_alternative_generate_sender_on_task() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        val expectedGenerateSender = false

        project.tasks.withType(CommunicationGenerationTask::class.java) {
            it.generateSender.set(expectedGenerateSender)
        }

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()
        task.generateSender.get().shouldBe(expectedGenerateSender)
    }

    @Test
    override fun with_alternative_generate_sender_on_extension() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        val expectedGenerateSender = false

        project.extensions.getByType(HermesExtension::class.java).communication
            .generateSender.set(expectedGenerateSender)

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()
        task.generateSender.get().shouldBe(expectedGenerateSender)
    }

    @Test
    override fun with_alternative_generate_consumer_on_task() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        val expectedGenerateConsumer = false

        project.tasks.withType(CommunicationGenerationTask::class.java) {
            it.generateConsumer.set(expectedGenerateConsumer)
        }

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()
        task.generateConsumer.get().shouldBe(expectedGenerateConsumer)
    }

    @Test
    override fun with_alternative_generate_consumer_on_extension() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        val expectedGenerateConsumer = false

        project.extensions.getByType(HermesExtension::class.java).communication
            .generateConsumer.set(expectedGenerateConsumer)

        project.evaluate()

        val task = project.taskInstance<CommunicationGenerationTask>()
        task.generateConsumer.get().shouldBe(expectedGenerateConsumer)
    }
}