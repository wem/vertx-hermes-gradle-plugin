package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.VertxJsonConfiguration
import io.vertx.core.json.JsonObject

object CommunicationParser {
    init {
        VertxJsonConfiguration()
    }

    fun parseDefinitions(json: String): List<CommunicationDefinition> {
        CommunicationJsonValidator.validate(json)
        return JsonObject(json).getJsonArray("communications")
            .map { (it as JsonObject).mapTo(CommunicationDefinition::class.java).validate() }
    }
}