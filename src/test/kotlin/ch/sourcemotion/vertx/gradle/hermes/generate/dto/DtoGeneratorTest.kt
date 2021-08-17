package ch.sourcemotion.vertx.gradle.hermes.generate.dto

import ch.sourcemotion.vertx.gradle.hermes.TestWithFixture
import ch.sourcemotion.vertx.gradle.hermes.generate.AbstractGeneratorTest
import ch.sourcemotion.vertx.gradle.hermes.generate.DtoClassInfo
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.nio.file.Paths

internal class DtoGeneratorTest : AbstractGeneratorTest(), TestWithFixture {

    private val sut = DtoGenerator()

    @Test
    internal fun generate_single_dto(@TempDir tempDir: Path) {
        val configuration = DtoGeneratorConfiguration(
            fixturePathOf("dto/generate/alpha-dto.json").toFile(),
            tempDir.toFile(),
            PACKAGE_NAME
        )
        val generatedClasses = sut.generate(configuration)
        tempDir.verifyAlphaDto()
        generatedClasses.shouldHaveSize(1).first().verify(PACKAGE_NAME, "Alpha")
    }

    @Test
    internal fun generate_multiple_dtos(@TempDir tempDir: Path) {
        val configuration = DtoGeneratorConfiguration(
            fixturePathOf("dto/generate").toFile(),
            tempDir.toFile(),
            PACKAGE_NAME
        )
        val generatedClasses = sut.generate(configuration)
        tempDir.verifyAlphaDto()
        tempDir.verifyBetaDto()
        generatedClasses.shouldHaveSize(2).asClue { classes ->
            classes.first { it.className == "Alpha" }.verify(PACKAGE_NAME, "Alpha")
            classes.first { it.className == "Beta" }.verify(PACKAGE_NAME, "Beta")
        }
    }

    private fun Path.verifyAlphaDto() {
        val kCLass = compileSourceFile(Paths.get("$this", *packageNameParts, "Alpha.kt"), "$PACKAGE_NAME.Alpha")
        kCLass.declaredFields.firstOrNull { it.name == "stringField" }.shouldNotBeNull()
    }

    private fun Path.verifyBetaDto() {
        val kCLass = compileSourceFile(Paths.get("$this", *packageNameParts, "Beta.kt"), "$PACKAGE_NAME.Beta")
        kCLass.declaredFields.firstOrNull { it.name == "intField" }.shouldNotBeNull()
    }

    private fun DtoClassInfo.verify(
        expectedPackageName: String,
        expectedClassName: String
    ) {
        packageName.shouldBe(expectedPackageName)
        expectedClassName.shouldBe(expectedClassName)
    }
}