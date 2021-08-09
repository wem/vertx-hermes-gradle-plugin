package ch.sourcemotion.vertx.gradle.hermes.generate.dto

import ch.sourcemotion.vertx.gradle.hermes.files.addPackage
import ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo
import ch.sourcemotion.vertx.gradle.hermes.generate.Generator
import net.pwall.json.schema.codegen.CodeGenerator
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.streams.toList

class DtoGenerator : Generator<DtoGeneratorConfiguration, List<DtoClassInfo>>() {
    override fun generate(configuration: DtoGeneratorConfiguration) : List<DtoClassInfo> {
        val outputPath = configuration.outputDir.toPath()
        val codeGenerator = CodeGenerator().apply {
            baseDirectoryName = "$outputPath"
            basePackageName = configuration.dtoPackage
        }
        codeGenerator.generate(configuration.inputDir)
        val generatedClassFilesPath = outputPath.addPackage(configuration.dtoPackage)
        return findGeneratedClasses(generatedClassFilesPath, configuration.dtoPackage)
    }

    private fun findGeneratedClasses(basePath: Path, packageName: String) : List<DtoClassInfo> {
        val generatedClassNames = Files.list(basePath).filter { it.name.endsWith("kt") }.map { it.name.substringBefore(".kt") }
        return generatedClassNames.map { DtoClassInfo(packageName, it) }.toList()
    }
}