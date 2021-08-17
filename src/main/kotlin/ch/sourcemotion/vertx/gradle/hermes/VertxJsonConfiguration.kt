package ch.sourcemotion.vertx.gradle.hermes

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.json.jackson.DatabindCodec

object VertxJsonConfiguration {
    private var configured = false

    operator fun invoke() {
        if (!configured) {
            DatabindCodec.mapper().registerKotlinModule().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            DatabindCodec.prettyMapper().registerKotlinModule().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            configured = true
        }
    }
}