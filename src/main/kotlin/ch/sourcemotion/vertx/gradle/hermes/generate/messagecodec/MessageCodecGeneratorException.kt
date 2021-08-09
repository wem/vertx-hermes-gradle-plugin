package ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec

import org.gradle.api.GradleException

class MessageCodecGeneratorException(message: String, cause: Throwable? = null) : GradleException(message, cause)