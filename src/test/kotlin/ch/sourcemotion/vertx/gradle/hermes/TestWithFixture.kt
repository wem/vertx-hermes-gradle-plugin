package ch.sourcemotion.vertx.gradle.hermes

import java.nio.file.Path
import java.nio.file.Paths

interface TestWithFixture {
    fun fixturePathOf(fixtureResourcePathValue: String): Path =
        Paths.get("${Paths.get(TestWithFixture::class.java.getResource("/fixture/$fixtureResourcePathValue").toURI())}")
}