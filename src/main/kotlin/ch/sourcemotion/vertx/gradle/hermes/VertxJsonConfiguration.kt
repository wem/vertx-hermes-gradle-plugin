package ch.sourcemotion.vertx.gradle.hermes

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.json.jackson.DatabindCodec

object VertxJsonConfiguration {
    private var configured = false

    operator fun invoke() {
        if (!configured) {
            DatabindCodec.mapper().registerKotlinModule()
            DatabindCodec.prettyMapper().registerKotlinModule()
            configured = true
        }
    }
}