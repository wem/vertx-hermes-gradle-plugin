package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.TestWithFixture
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CommunicationJsonValidatorTest : TestWithFixture {
    @Test
    internal fun missing_name_validation() {
        shouldThrow<JsonValidationException> { CommunicationJsonValidator.validate(fixtureTextContent("/communication/missing_address.json")) }
    }

    @Test
    internal fun valid_all_properties() {
        shouldNotThrow<JsonValidationException> { CommunicationJsonValidator.validate(fixtureTextContent("/communication/all_properties.json")) }
    }

    @Test
    internal fun valid_only_required_properties() {
        shouldNotThrow<JsonValidationException> { CommunicationJsonValidator.validate(fixtureTextContent("/communication/only_required_properties.json")) }
    }
}