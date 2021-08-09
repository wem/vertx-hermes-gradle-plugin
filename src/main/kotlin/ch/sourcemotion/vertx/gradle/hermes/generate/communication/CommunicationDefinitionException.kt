package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import org.gradle.api.GradleException

/**
 * Thrown if a user provided communication specification json file was invalid.
 */
class CommunicationDefinitionException(message: String, cause: Throwable? = null) : GradleException(message, cause)