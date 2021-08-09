package ch.sourcemotion.vertx.gradle.hermes.files

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.SourceSet
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.name

fun String.toPath(): Path = Path(this)

fun Path.add(file: File) = this.add(file.toPath())

fun Path.add(other: Path): Path {
    if (other.isAbsolute) {
        throw IllegalArgumentException("Absolute path $other can not concatenated to parent $this")
    }
    return Paths.get("$this", *other.map { it.name }.toTypedArray())
}

fun Path.addPackage(packageName: String): Path = Paths.get("$this", *packageName.split(".").toTypedArray())

fun DirectoryProperty.evaluateAbsoluteProjectPath(
    projectPath: Path,
    prioritizedDirectory: DirectoryProperty,
    relativeDefaultPath: Path
): Path {
    val pathAccordingConfiguration = if (prioritizedDirectory.isPresent) {
        prioritizedDirectory.get().asFile.toPath()
    } else if (isPresent) {
        get().asFile.toPath()
    } else relativeDefaultPath
    return if (pathAccordingConfiguration.isAbsolute) {
        pathAccordingConfiguration
    } else {
        projectPath.add(pathAccordingConfiguration)
    }
}