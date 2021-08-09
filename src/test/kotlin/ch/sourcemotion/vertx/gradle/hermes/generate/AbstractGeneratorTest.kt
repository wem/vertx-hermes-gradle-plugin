package ch.sourcemotion.vertx.gradle.hermes.generate

import ch.sourcemotion.vertx.gradle.hermes.VertxJsonConfiguration
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import java.nio.file.Path

abstract class AbstractGeneratorTest {

    @BeforeEach
    internal fun setUpVertxJson() {
        VertxJsonConfiguration()
    }

    fun compileSourceFile(sourceFilePath: Path, fullQualifiedClassNameToLoad: String): Class<*> {
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.fromPath(sourceFilePath.toFile()))
            inheritClassPath = true
        }.compile()

        result.exitCode.shouldBe(KotlinCompilation.ExitCode.OK)
        return result.classLoader.loadClass(fullQualifiedClassNameToLoad)
    }

    fun compileSourceFile(sourceFilePath: Path, vararg fullQualifiedClassNamesToLoad: String): Map<String, Class<*>> {
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.fromPath(sourceFilePath.toFile()))
            inheritClassPath = true
        }.compile()

        result.exitCode.shouldBe(KotlinCompilation.ExitCode.OK)
        return fullQualifiedClassNamesToLoad.associateWith { result.classLoader.loadClass(it) }
    }
}