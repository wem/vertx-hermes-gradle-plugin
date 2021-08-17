package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import org.gradle.api.GradleException

open class CommunicationException(message: String, cause: Throwable? = null) : GradleException(message, cause)

class JsonValidationException(message: String) : CommunicationException(message)
