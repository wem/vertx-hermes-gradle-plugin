package ch.sourcemotion.vertx.gradle.hermes.plugin

import org.gradle.api.GradleException

abstract class HermesException(message: String, cause: Throwable? = null) : GradleException(message, cause)