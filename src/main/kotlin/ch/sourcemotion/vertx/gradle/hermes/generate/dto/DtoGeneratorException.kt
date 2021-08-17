package ch.sourcemotion.vertx.gradle.hermes.generate.dto

import ch.sourcemotion.vertx.gradle.hermes.plugin.HermesException

class DtoGeneratorException(message: String, cause: Throwable? = null) : HermesException(message, cause)