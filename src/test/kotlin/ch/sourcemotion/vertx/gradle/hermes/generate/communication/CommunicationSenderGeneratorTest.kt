package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.TestWithFixture
import ch.sourcemotion.vertx.gradle.hermes.generate.AbstractGeneratorTest
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Paths

internal class CommunicationSenderGeneratorTest : AbstractGeneratorTest(), TestWithFixture {
    @Test
    internal fun generate(@TempDir tempDir: File) {
        val inputDir = fixturePathOf("communication/generator/definitions.json").toFile().parentFile
        val configuration = CommunicationGeneratorConfiguration(inputDir, tempDir, PACKAGE_NAME)
        CommunicationSenderGenerator().generate(configuration)

        val sourceFilePath = Paths.get(
            "${tempDir.toPath()}",
            *packageNameParts,
            "${CommunicationSenderGenerator.OUTPUT_FILE_NAME}.kt"
        )
        compileSourceFile(
            sourceFilePath, "$PACKAGE_NAME.${CommunicationSenderGenerator.OUTPUT_FILE_NAME}Kt"
        )

        writeSourceToFile("/ch/sourcemotion/HermesCommunicationSend.kt", sourceFilePath.toFile().readText())
    }
}