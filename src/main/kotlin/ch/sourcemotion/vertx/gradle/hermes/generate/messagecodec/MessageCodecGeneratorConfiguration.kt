package ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec

import ch.sourcemotion.vertx.gradle.hermes.generate.GeneratorConfiguration
import ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo
import java.io.File
import java.util.function.Function

data class MessageCodecGeneratorConfiguration(
    val outputDir: File,
    val packageName: String,
    val codecsFileName: String,
    val classInfos: List<DtoClassInfo>,
    val messageCodecNameSupplier: Function<DtoClassInfo, String>
) : GeneratorConfiguration