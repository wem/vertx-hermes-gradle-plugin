package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.generate.GeneratorConfiguration
import java.io.File

data class CommunicationGeneratorConfiguration(val inputDir: File, val outputDir: File, val packageName: String) :
    GeneratorConfiguration