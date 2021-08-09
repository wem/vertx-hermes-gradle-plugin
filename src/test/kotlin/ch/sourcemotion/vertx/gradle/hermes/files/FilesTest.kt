package ch.sourcemotion.vertx.gradle.hermes.files

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

internal class FilesTest {

    @Test
    internal fun path_with_file() {
        "root".toPath().add(File("ch")).shouldBe(Paths.get("root/ch"))
    }

    @Test
    internal fun path_with_deeper_file_path() {
        "root".toPath().add(File("ch/foo/bar")).shouldBe(Paths.get("root/ch/foo/bar"))
    }

    @Test
    internal fun path_with_sub_path() {
        "root".toPath().add("ch".toPath()).shouldBe(Paths.get("root/ch"))
    }

    @Test
    internal fun path_with_deeper_sub_path() {
        "root".toPath().add("ch/foo/bar".toPath()).shouldBe(Paths.get("root/ch/foo/bar"))
    }

    @Test
    internal fun path_with_root_sub_path() {
        shouldThrow<IllegalArgumentException> { "root".toPath().add("/ch".toPath()) }
    }

    @Test
    internal fun path_with_package() {
        "root".toPath().addPackage("ch").shouldBe(Paths.get("root/ch"))
    }

    @Test
    internal fun deeper_path_with_package() {
        "root/foo/bar".toPath().addPackage("ch").shouldBe(Paths.get("root/foo/bar/ch"))
    }

    @Test
    internal fun with_deeper_package() {
        "root".toPath().addPackage("ch.sourcemotion.foo.bar").shouldBe(Paths.get("root/ch/sourcemotion/foo/bar"))
    }
}