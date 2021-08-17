package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.TestWithFixture
import io.kotest.assertions.asClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CommunicationParserTest : TestWithFixture {

    private companion object {
        const val EXPECTED_NAME = "someName"
        const val EXPECTED_ADDRESS = "some-address"
        const val EXPECTED_SEND_MESSAGE_TYPE = "java.lang.String"
        const val EXPECTED_REPLY_MESSAGE_TYPE = "java.lang.String"
        const val EXPECTED_LOCAL = true
    }

    @Test
    internal fun parse_with_all_properties() {
        val json = fixtureTextContent("/communication/all_properties.json")
        val definitions = CommunicationParser.parseDefinitions(json)
        definitions.shouldHaveSize(1).first().asClue { def ->
            def.verify(
                EXPECTED_NAME,
                EXPECTED_ADDRESS,
                SendType.REQUEST,
                EXPECTED_SEND_MESSAGE_TYPE,
                EXPECTED_REPLY_MESSAGE_TYPE,
                EXPECTED_LOCAL
            )
        }
    }

    @Test
    internal fun parse_with_multiple_definitions() {
        val json = fixtureTextContent("/communication/multiple_definitions.json")
        val definitions = CommunicationParser.parseDefinitions(json)
        definitions.shouldHaveSize(2)
        definitions.first().asClue { def ->
            def.verify(
                EXPECTED_NAME,
                EXPECTED_ADDRESS,
                SendType.REQUEST,
                EXPECTED_SEND_MESSAGE_TYPE,
                EXPECTED_REPLY_MESSAGE_TYPE,
                EXPECTED_LOCAL
            )
        }
        definitions.last().asClue { def ->
            def.verify(
                "${EXPECTED_NAME}2",
                "${EXPECTED_ADDRESS}2",
                SendType.REQUEST,
                "${EXPECTED_SEND_MESSAGE_TYPE}2",
                "${EXPECTED_REPLY_MESSAGE_TYPE}2",
                false
            )
        }
    }

    @Test
    internal fun parse_with_only_required_properties() {
        val json = fixtureTextContent("/communication/only_required_properties.json")
        val definitions = CommunicationParser.parseDefinitions(json)
        definitions.shouldHaveSize(1).first().asClue { def ->
            def.name.shouldBe(EXPECTED_NAME)
            def.address.shouldBe(EXPECTED_ADDRESS)
            def.sendType.shouldBe(SendType.SEND)
            def.sendMessageType.shouldBeNull()
            def.replyMessageType.shouldBeNull()
            def.localOnly.shouldBeFalse()
        }
    }

    private fun CommunicationDefinition.verify(
        expectedName: String,
        expectedAddress: String,
        expectedSendType: SendType,
        expectedSendMessageType: String,
        expectedReplyMessageType: String,
        expectedLocal: Boolean
    ) {
        name.shouldBe(expectedName)
        address.shouldBe(expectedAddress)
        sendType.shouldBe(expectedSendType)
        sendMessageType.shouldBe(expectedSendMessageType)
        replyMessageType.shouldBe(expectedReplyMessageType)
        localOnly.shouldBe(expectedLocal)
    }
}