package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.*
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class HermesCommunicationTest  {

    private companion object {
        const val BODY = "some-body"
    }

    @Test
    internal fun send_with_send_message_type(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint()
        val eventBus = vertx.eventBus()
        eventBus.sendWithSendMessageTypeConsumer {
            testContext.verify {
                shouldBeSend()
                shouldHaveExpectedBody()
            }
            checkpoint.flag()
        }
        eventBus.sendSendWithSendMessageType(BODY)
    }

    @Test
    internal fun send_without_send_or_reply_message_type(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint()
        val eventBus = vertx.eventBus()
        eventBus.sendWithoutSendOrReplyMessageTypeConsumer {
            testContext.verify {
                shouldBeSend()
                shouldHaveNullBody()
            }
            checkpoint.flag()
        }
        eventBus.sendSendWithoutSendOrReplyMessageType()
    }

    @Test
    internal fun send_local_only(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint()
        val eventBus = vertx.eventBus()
        eventBus.sendLocalOnlyConsumer {
            testContext.verify {
                shouldBeSend()
                shouldHaveNullBody()
            }
            checkpoint.flag()
        }
        eventBus.sendSendLocalOnly()
    }

    @Test
    internal fun publish_with_send_message_type(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint()
        val eventBus = vertx.eventBus()
        eventBus.publishWithSendMessageTypeConsumer {
            testContext.verify {
                shouldNotBeSend()
                shouldHaveExpectedBody()
            }
            checkpoint.flag()
        }
        eventBus.publishPublishWithSendMessageType(BODY)
    }

    @Test
    internal fun publish_without_send_or_reply_message_type(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint()
        val eventBus = vertx.eventBus()
        eventBus.publishWithoutSendOrReplyMessageTypeConsumer {
            testContext.verify {
                shouldNotBeSend()
                shouldHaveNullBody()
            }
            checkpoint.flag()
        }
        eventBus.publishPublishWithoutSendOrReplyMessageType()
    }

    @Test
    internal fun publish_local_only(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint()
        val eventBus = vertx.eventBus()
        eventBus.publishLocalOnlyConsumer {
            testContext.verify {
                shouldNotBeSend()
                shouldHaveNullBody()
            }
            checkpoint.flag()
        }
        eventBus.publishPublishLocalOnly()
    }

    @Test
    internal fun request_with_reply_message_type(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint(2)
        val eventBus = vertx.eventBus()
        eventBus.requestWithReplyMessageTypeConsumer {
            testContext.verify {
                shouldBeSend()
                shouldHaveNullBody()
            }
            checkpoint.flag()
            BODY
        }
        GlobalScope.launch(vertx.dispatcher()) {
            val response = eventBus.requestRequestWithReplyMessageType()
            response.shouldHaveExpectedBody()
            checkpoint.flag()
        }
    }

    @Test
    internal fun request_with_send_message_type(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint(2)
        val eventBus = vertx.eventBus()
        eventBus.requestWithSendMessageTypeConsumer {
            testContext.verify {
                shouldBeSend()
                shouldHaveExpectedBody()
            }
            checkpoint.flag()
        }
        GlobalScope.launch(vertx.dispatcher()) {
            val response = eventBus.requestRequestWithSendMessageType(BODY)
            testContext.verify { response.shouldHaveNullBody() }
            checkpoint.flag()
        }
    }


    @Test
    internal fun request_with_send_and_reply_message_type(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint(2)
        val eventBus = vertx.eventBus()
        eventBus.requestWithSendAndReplyMessageTypeConsumer {
            testContext.verify {
                shouldBeSend()
                shouldHaveExpectedBody()
            }
            checkpoint.flag()
            BODY
        }
        GlobalScope.launch(vertx.dispatcher()) {
            val response = eventBus.requestRequestWithSendAndReplyMessageType(BODY)
            testContext.verify { response.shouldHaveExpectedBody() }
            checkpoint.flag()
        }
    }

    @Test
    internal fun request_without_send_or_reply_message_type(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint(2)
        val eventBus = vertx.eventBus()
        eventBus.requestWithoutSendOrReplyMessageTypeConsumer {
            testContext.verify {
                shouldBeSend()
                shouldHaveNullBody()
            }
            checkpoint.flag()
        }
        GlobalScope.launch(vertx.dispatcher()) {
            val response = eventBus.requestRequestWithoutSendOrReplyMessageType()
            testContext.verify { response.shouldHaveNullBody() }
            checkpoint.flag()
        }
    }



    private fun Message<*>.shouldBeSend() = isSend.shouldBeTrue()
    private fun Message<*>.shouldNotBeSend() = isSend.shouldBeFalse()
    private fun Message<*>.shouldHaveExpectedBody() = body().shouldBe(BODY)
    private fun Message<*>.shouldHaveNullBody() = body().shouldBeNull()
}