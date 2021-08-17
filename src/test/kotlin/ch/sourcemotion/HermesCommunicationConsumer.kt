package ch.sourcemotion

import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import kotlin.String
import kotlin.Unit

public fun EventBus.sendWithSendMessageTypeConsumer(block: Message<String>.() -> Unit): Unit {
  consumer<String>("send-WithSendMessageType", block)
}

public fun EventBus.sendWithoutSendOrReplyMessageTypeConsumer(block: Message<Unit>.() -> Unit):
    Unit {
  consumer<Unit>("send-WithoutSendOrReplyMessageType", block)
}

public fun EventBus.sendLocalOnlyConsumer(block: Message<Unit>.() -> Unit): Unit {
  consumer<Unit>("send-LocalOnly", block)
}

public fun EventBus.publishWithSendMessageTypeConsumer(block: Message<String>.() -> Unit): Unit {
  consumer<String>("publish-WithSendMessageType", block)
}

public fun EventBus.publishWithoutSendOrReplyMessageTypeConsumer(block: Message<Unit>.() -> Unit):
    Unit {
  consumer<Unit>("publish-WithoutSendOrReplyMessageType", block)
}

public fun EventBus.publishLocalOnlyConsumer(block: Message<Unit>.() -> Unit): Unit {
  consumer<Unit>("publish-LocalOnly", block)
}

public fun EventBus.requestWithReplyMessageTypeConsumer(block: Message<Unit>.() -> String?): Unit {
  consumer<Unit>("request-WithReplyMessageType") {
  val result = it.block()
  it.reply(result) 
  }
}

public fun EventBus.requestWithSendMessageTypeConsumer(block: Message<String>.() -> Unit): Unit {
  consumer<String>("request-WithSendMessageType") {
  it.block()
  it.reply(null) 
  }
}

public fun EventBus.requestWithSendAndReplyMessageTypeConsumer(block: Message<String>.() -> String):
    Unit {
  consumer<String>("request-WithSendAndReplyMessageType") {
  val result = it.block()
  it.reply(result) 
  }
}

public fun EventBus.requestWithoutSendOrReplyMessageTypeConsumer(block: Message<Unit>.() -> Unit):
    Unit {
  consumer<Unit>("request-WithoutSendOrReplyMessageType") {
  it.block()
  it.reply(null) 
  }
}

public fun EventBus.requestLocalOnlyConsumer(block: Message<Unit>.() -> Unit): Unit {
  localConsumer<Unit>("request-LocalOnly") {
  it.block()
  it.reply(null) 
  }
}
