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

    private companion object {
        const val PACKAGE = "ch.sourcemotion.vertx.gradle.hermes.test"
        val packageParts = PACKAGE.split(".").toTypedArray()
    }

    private val sut = DtoGenerator()

    @Test
    internal fun generate_single_dto(@TempDir tempDir: Path) {
        val configuration = DtoGeneratorConfiguration(
            fixturePathOf("dto/generate/alpha-dto.json").toFile(),
            tempDir.toFile(),
            PACKAGE
        )
        val generatedClasses = sut.generate(configuration)
        tempDir.verifyAlphaDto()
        generatedClasses.shouldHaveSize(1).first().verify(PACKAGE, "Alpha")
    }

    @Test
    internal fun generate_multiple_dtos(@TempDir tempDir: Path) {
        val configuration = DtoGeneratorConfiguration(
            fixturePathOf("dto/generate").toFile(),
            tempDir.toFile(),
            PACKAGE
        )
        val generatedClasses = sut.generate(configuration)
        tempDir.verifyAlphaDto()
        tempDir.verifyBetaDto()
        generatedClasses.shouldHaveSize(2).asClue { classes ->
            classes.first { it.className == "Alpha" }.verify(PACKAGE, "Alpha")
            classes.first { it.className == "Beta" }.verify(PACKAGE, "Beta")
        }
    }

    private fun Path.verifyAlphaDto() {
        val kCLass = compileSourceFile(Paths.get("$this", *packageParts, "Alpha.kt"), "$PACKAGE.Alpha")
        kCLass.declaredFields.firstOrNull { it.name == "stringField" }.shouldNotBeNull()
    }

    private fun Path.verifyBetaDto() {
        val kCLass = compileSourceFile(Paths.get("$this", *packageParts, "Beta.kt"), "$PACKAGE.Beta")
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