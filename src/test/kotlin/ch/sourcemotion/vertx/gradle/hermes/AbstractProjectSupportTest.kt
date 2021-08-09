package ch.sourcemotion.vertx.gradle.hermes

import ch.sourcemotion.vertx.gradle.hermes.files.add
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGenerationTask
import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesExtension
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.SourceSet
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

abstract class AbstractProjectSupportTest {

    protected fun projectWith(plugins: List<Class<out Plugin<Project>>>) : Project {
        val project = ProjectBuilder.builder().build()
        plugins.forEach { pluginClass -> project.pluginManager.apply(pluginClass) }
        return project
    }

    protected fun Project.evaluate() {
        val name = "evaluationTask"
        tasks.register(name)
        getTasksByName(name, false)
    }

    fun copyFolder(src: Path, dest: Path) {
        Files.walk(src).use { stream ->
            stream.forEach { source ->
                println("Copy $source to $dest")
                Files.copy(source, dest.resolve(src.relativize(source)))
            }
        }
    }

    protected fun executeBuildAndVerify(projectDir: File, taskName: String, expectedTaskOutcome: TaskOutcome) {
        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withArguments(taskName)
            .build()
        val taskResult = result.task(":$taskName").shouldNotBeNull()
        taskResult.outcome.shouldBe(expectedTaskOutcome)
    }


    protected fun Project.sourceSetToVerify() = extensions.getByType(HermesExtension::class.java).evaluateSourceSet()
    protected fun Project.inputDirectoryPropertyOf(path: Path): DirectoryProperty = directoryPropertyOf(projectDir.toPath(), path)
    protected fun Project.outputDirectoryPropertyOf(path: Path): DirectoryProperty = directoryPropertyOf(buildDir.toPath(), path)

    private fun Project.directoryPropertyOf(projectPath: Path, path: Path): DirectoryProperty = objects.directoryProperty().apply { set(projectPath.add(path).toFile()) }

    protected fun SourceSet.verifySourceDir(expectedSourcePath: Path) {
        allJava.srcDirs.shouldContain(expectedSourcePath.toFile())
    }
}