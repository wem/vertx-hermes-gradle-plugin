package ch.sourcemotion

import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.kotlin.core.eventbus.deliveryOptionsOf
import io.vertx.kotlin.coroutines.await
import kotlin.String
import kotlin.Unit

public fun EventBus.sendSendWithSendMessageType(message: String): Unit {
  send("send-WithSendMessageType", message, deliveryOptionsOf(localOnly = false))
}

public fun EventBus.sendSendWithoutSendOrReplyMessageType(): Unit {
  send("send-WithoutSendOrReplyMessageType", null, deliveryOptionsOf(localOnly = false))
}

public fun EventBus.sendSendLocalOnly(): Unit {
  send("send-LocalOnly", null, deliveryOptionsOf(localOnly = true))
}

public fun EventBus.publishPublishWithSendMessageType(message: String): Unit {
  publish("publish-WithSendMessageType", message, deliveryOptionsOf(localOnly = false))
}

public fun EventBus.publishPublishWithoutSendOrReplyMessageType(): Unit {
  publish("publish-WithoutSendOrReplyMessageType", null, deliveryOptionsOf(localOnly = false))
}

public fun EventBus.publishPublishLocalOnly(): Unit {
  publish("publish-LocalOnly", null, deliveryOptionsOf(localOnly = true))
}

public suspend fun EventBus.requestRequestWithReplyMessageType(): Message<String?> =
    request<String?>("request-WithReplyMessageType", null, deliveryOptionsOf(localOnly =
    false)).await()

public suspend fun EventBus.requestRequestWithSendMessageType(message: String): Message<Unit> =
    request<Unit>("request-WithSendMessageType", message, deliveryOptionsOf(localOnly =
    false)).await()

public suspend fun EventBus.requestRequestWithSendAndReplyMessageType(message: String):
    Message<String> = request<String>("request-WithSendAndReplyMessageType", message,
    deliveryOptionsOf(localOnly = false)).await()

public suspend fun EventBus.requestRequestWithoutSendOrReplyMessageType(): Message<Unit> =
    request<Unit>("request-WithoutSendOrReplyMessageType", null, deliveryOptionsOf(localOnly =
    false)).await()

public suspend fun EventBus.requestRequestLocalOnly(): Message<Unit> =
    request<Unit>("request-LocalOnly", null, deliveryOptionsOf(localOnly = true)).await()
