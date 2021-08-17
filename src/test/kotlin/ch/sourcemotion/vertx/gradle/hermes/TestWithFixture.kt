package ch.sourcemotion.vertx.gradle.hermes

import java.nio.file.Path
import java.nio.file.Paths

interface TestWithFixture {
    fun fixturePathOf(fixtureResourcePathValue: String): Path =
        Paths.get(TestWithFixture::class.java.getResource("/fixture/$fixtureResourcePathValue").toURI())

    fun fixtureTextContent(fixtureResourcePathValue: String): String =
        fixturePathOf(fixtureResourcePathValue).toFile().inputStream()
            .use { ins -> ins.bufferedReader().use { reader -> reader.readText() } }
}