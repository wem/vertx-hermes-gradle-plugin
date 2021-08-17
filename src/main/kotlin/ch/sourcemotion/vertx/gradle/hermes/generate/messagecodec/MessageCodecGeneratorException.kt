package ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec

import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesException

class MessageCodecGeneratorException(message: String, cause: Throwable? = null) : HermesException(message, cause)