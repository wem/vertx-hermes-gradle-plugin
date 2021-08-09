package ch.sourcemotion.vertx.gradle.hermes.generate

import ch.sourcemotion.vertx.gradle.hermes.files.evaluateAbsoluteProjectPath
import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGeneratorTaskInternalOutput
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.nio.file.Path
import java.util.function.Function

interface DefinesPackageName {
    val packageName: Property<String>

    fun applyFinalPackageName(alternative: DefinesPackageName) {
        if (!packageName.isPresent) {
            packageName.set(alternative.packageName)
        }
    }
}

interface DefinesInputDir {
    val inputDir: DirectoryProperty

    fun applyFinalInputPath(projectPath: Path, alternative: DefinesInputDir, relativeDefaultOutputPath: Path) {
        val finalInputPath = runCatching {
            alternative.inputDir.evaluateAbsoluteProjectPath(projectPath, inputDir, relativeDefaultOutputPath)
        }.getOrElse { throw GradleException("Failed to evaluate input dir", it) }
        inputDir.set(finalInputPath.toFile())
    }
}

interface DefinesOutputDir {
    val outputDir: DirectoryProperty

    fun applyFinalOutputPath(projectPath: Path, alternative: DefinesOutputDir, relativeDefaultOutputPath: Path): Path {
        val finalOutputPath = runCatching {
            alternative.outputDir.evaluateAbsoluteProjectPath(projectPath, outputDir, relativeDefaultOutputPath)
        }.getOrElse { throw GradleException("Failed to evaluate output dir", it) }
        return finalOutputPath.also { outputDir.set(it.toFile()) }
    }
}

interface DefinesClassesInfo {
    val classesInfo: ListProperty<DtoClassInfo>

    fun applyFinalClassesInfo(
        project: Project,
        alternative: DefinesClassesInfo,
        dtoGenOutput: Provider<DtoGeneratorTaskInternalOutput>
    ) {
        val generatedClassesInfo =
            if (dtoGenOutput.isPresent && dtoGenOutput.get().generatedClasses.isPresent) {
                dtoGenOutput.get().generatedClasses.get()
            } else emptyList()
        val classesInfoByConfig = if (classesInfo.isPresent && classesInfo.get().isNotEmpty()) {
            classesInfo.get()
        } else if (alternative.classesInfo.isPresent) {
            alternative.classesInfo.get()
        } else emptyList()
        val finalClassesInfo = (generatedClassesInfo + classesInfoByConfig).distinct()
        classesInfo.set(finalClassesInfo)
    }
}

interface DefinesMessageCodecsFileName {
    val messageCodecsFileName: Property<String>

    fun applyFinalMessageCodecsFileName(override: DefinesMessageCodecsFileName) {
        if (override.messageCodecsFileName.isPresent) {
            messageCodecsFileName.set(override.messageCodecsFileName)
        }
    }
}

interface DefinesMessageCodecNameSupplier {
    val messageCodecNameSupplier: Property<Function<DtoClassInfo, String>>

    fun applyFinalDefinesMessageCodecNameSupplier(override: DefinesMessageCodecNameSupplier) {
        if (override.messageCodecNameSupplier.isPresent) {
            messageCodecNameSupplier.set(override.messageCodecNameSupplier)
        }
    }
}