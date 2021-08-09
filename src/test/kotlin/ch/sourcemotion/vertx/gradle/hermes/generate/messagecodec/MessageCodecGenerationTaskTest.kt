package ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec

import ch.sourcemotion.vertx.gradle.hermes.*
import ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGenerationTask
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGenerationTaskTest
import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesExtension
import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesPlugin
import io.kotest.assertions.asClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Paths

internal class MessageCodecGenerationTaskTest : AbstractProjectSupportTest(), DefaultTaskTest, DefinesPackageNameImplTest, DefinesOutputDirImplTest,
    DefinesClassesInfoImplTest {

    @Test
    override fun with_default_values() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        project.evaluate()

        val sourceSet = project.sourceSetToVerify()
        val task = project.tasks.withType(MessageCodecGenerationTask::class.java).shouldHaveSize(1).first()
        task.outputDir.get()
            .shouldBe(project.outputDirectoryPropertyOf(task.createDefaultRelativeOutputPath(sourceSet)).get())
        task.packageName.isPresent.shouldBeFalse()
        task.classesInfo.isPresent.shouldBeTrue()
        task.messageCodecsFileName.asClue {
            it.isPresent.shouldBeTrue()
            it.get().shouldBe(MessageCodecGenerationTask.DEFAULT_MESSAGE_CODEC_FILE_NAME)
        }
        task.messageCodecNameSupplier.asClue {
            it.isPresent.shouldBeTrue()
            it.get().shouldBe(MessageCodecGenerationTask.DEFAULT_MESSAGE_CODEC_NAME_SUPPLIER)
        }
    }

    override fun disabled() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        project.tasks.withType(MessageCodecGenerationTask::class.java) {
            it.enabled = false
        }
        project.evaluate()

        val task = project.tasks.withType(MessageCodecGenerationTask::class.java).shouldHaveSize(1).first()

        task.outputDir.isPresent.shouldBeFalse()
        task.packageName.isPresent.shouldBeFalse()
        task.messageCodecsFileName.isPresent.shouldBeFalse()
        task.messageCodecNameSupplier.isPresent.shouldBeFalse()
    }

    @Test
    override fun with_package_name_on_task() {
        val expectedPackageName = "ch.sourcemotion"

        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        project.tasks.withType(MessageCodecGenerationTask::class.java) {
            it.packageName.set(expectedPackageName)
        }

        project.evaluate()

        val task = project.tasks.withType(MessageCodecGenerationTask::class.java).shouldHaveSize(1).first()
        task.packageName.get().shouldBe(expectedPackageName)
    }

    @Test
    override fun with_package_name_on_extension() {
        val expectedPackageName = "ch.sourcemotion"

        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))
        project.extensions.getByType(HermesExtension::class.java).codec.packageName.set(expectedPackageName)

        project.evaluate()

        val task = project.tasks.withType(MessageCodecGenerationTask::class.java).shouldHaveSize(1).first()
        task.packageName.get().shouldBe(expectedPackageName)
    }

    @Test
    override fun with_alternative_output_dir_on_task() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        val expectedOutputDir = File("${project.buildDir}/some-other-build-dir")
        project.tasks.withType(MessageCodecGenerationTask::class.java) {
            it.outputDir.set(project.objects.directoryProperty().apply { set(File("${project.buildDir}/some-other-build-dir")) })
        }

        project.evaluate()

        val sourceSet = project.sourceSetToVerify()
        val task = project.tasks.withType(MessageCodecGenerationTask::class.java).shouldHaveSize(1).first()
        task.outputDir.get().asFile.shouldBe(expectedOutputDir)
        sourceSet.verifySourceDir(expectedOutputDir.toPath())
    }

    @Test
    override fun with_alternative_output_dir_on_extension() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        val expectedOutputDir = File("${project.buildDir}/some-other-build-dir")
        project.extensions.getByType(HermesExtension::class.java).codec.outputDir.set(
            project.objects.directoryProperty().apply { set(File("${project.buildDir}/some-other-build-dir")) })

        project.evaluate()

        val sourceSet = project.sourceSetToVerify()
        val task = project.tasks.withType(MessageCodecGenerationTask::class.java).shouldHaveSize(1).first()
        task.outputDir.get().asFile.shouldBe(expectedOutputDir)
        sourceSet.verifySourceDir(expectedOutputDir.toPath())
    }

    @Test
    override fun with_alternative_classes_info_on_extension() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        val expectedDtoClass = DtoClassInfo("ch.sourcemotion", "TestDto")
        project.extensions.getByType(HermesExtension::class.java).codec.classesInfo.set(listOf(expectedDtoClass))

        project.evaluate()

        val task = project.tasks.withType(MessageCodecGenerationTask::class.java).shouldHaveSize(1).first()
        task.classesInfo.isPresent.shouldBeTrue()
        task.classesInfo.get().shouldContainExactly(expectedDtoClass)
    }

    @Test
    override fun with_alternative_classes_info_on_task() {
        val project = projectWith(listOf(JavaPlugin::class.java, HermesPlugin::class.java))

        val expectedDtoClass = DtoClassInfo("ch.sourcemotion", "TestDto")
        project.tasks.withType(MessageCodecGenerationTask::class.java) {
            it.classesInfo.set(listOf(expectedDtoClass))
        }

        project.evaluate()

        val task = project.tasks.withType(MessageCodecGenerationTask::class.java).shouldHaveSize(1).first()
        task.classesInfo.isPresent.shouldBeTrue()
        task.classesInfo.get().shouldContainExactly(expectedDtoClass)
    }

    @Test
    override fun execute_task(@TempDir tempDir: File) {
        val projectSourceBase = DtoGenerationTaskTest::class.java.getResource("/fixture/codec/task")
            ?: throw IllegalStateException("Message codec gen task execution test fixtures not found")
        val projectDir = File(tempDir, "project")
        copyFolder(Paths.get(projectSourceBase.toURI()), projectDir.toPath())
        executeBuildAndVerify(projectDir, MessageCodecGenerationTask.NAME, TaskOutcome.SUCCESS)
        executeBuildAndVerify(projectDir, MessageCodecGenerationTask.NAME, TaskOutcome.UP_TO_DATE)
    }
}