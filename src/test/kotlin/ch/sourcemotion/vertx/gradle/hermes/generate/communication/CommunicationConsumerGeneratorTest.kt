package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.TestWithFixture
import ch.sourcemotion.vertx.gradle.hermes.generate.AbstractGeneratorTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Paths

internal class CommunicationConsumerGeneratorTest : AbstractGeneratorTest(), TestWithFixture {
    @Test
    internal fun generate(@TempDir tempDir: File) {
        val inputFile = fixturePathOf("communication/generator/definitions.json").toFile()
        val configuration = CommunicationGeneratorConfiguration(inputFile, tempDir, PACKAGE_NAME)
        CommunicationConsumerGenerator().generate(configuration)

        val sourceFilePath = Paths.get(
            "${tempDir.toPath()}",
            *packageNameParts,
            "${CommunicationConsumerGenerator.OUTPUT_FILE_NAME}.kt"
        )
        compileSourceFile(
            sourceFilePath,
            "$PACKAGE_NAME.${CommunicationConsumerGenerator.OUTPUT_FILE_NAME}Kt"
        )

        writeSourceToFile("/ch/sourcemotion/HermesCommunicationConsumer.kt", sourceFilePath.toFile().readText())
    }
}
