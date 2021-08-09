package ch.sourcemotion.vertx.gradle.hermes.generate.dto

import ch.sourcemotion.vertx.gradle.hermes.generate.GeneratorConfiguration
import java.io.File

data class DtoGeneratorConfiguration(val inputDir: File, val outputDir: File, val dtoPackage: String) :
    GeneratorConfiguration