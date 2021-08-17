package ch.sourcemotion.vertx.gradle.hermes.generate

import ch.sourcemotion.vertx.gradle.hermes.VertxJsonConfiguration
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotest.assertions.asClue
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import java.nio.file.Path
import java.nio.file.Paths

abstract class AbstractGeneratorTest {

    companion object {
        const val PACKAGE_NAME = "ch.sourcemotion"
        val packageNameParts = PACKAGE_NAME.split(".").toTypedArray()
    }

    @BeforeEach
    internal fun setUpVertxJson() {
        VertxJsonConfiguration()
    }

    protected fun writeSourceToFile(filePath: String, fileContent: String) {
        Paths.get(System.getProperty("user.dir"), "src", "test", "kotlin", filePath).toFile().writeText(fileContent)
    }


    fun compileSourceFile(sourceFilePath: Path, fullQualifiedClassNameToLoad: String): Class<*> {
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.fromPath(sourceFilePath.toFile()))
            inheritClassPath = true
        }.compile()

        withClue(result.messages) {
            result.exitCode.shouldBe(KotlinCompilation.ExitCode.OK)
        }
        return result.classLoader.loadClass(fullQualifiedClassNameToLoad)
    }

    fun compileSourceFile(sourceFilePath: Path, vararg fullQualifiedClassNamesToLoad: String): Map<String, Class<*>> {
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.fromPath(sourceFilePath.toFile()))
            inheritClassPath = true
        }.compile()

        withClue(result.messages) {
            result.exitCode.shouldBe(KotlinCompilation.ExitCode.OK)
        }
        return fullQualifiedClassNamesToLoad.associateWith { result.classLoader.loadClass(it) }
    }
}