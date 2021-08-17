package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesException

open class CommunicationException(message: String, cause: Throwable? = null) : HermesException(message, cause)

class JsonValidationException(message: String) : CommunicationException(message)
