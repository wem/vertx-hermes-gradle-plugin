package ch.sourcemotion.vertx.gradle.hermes.plugin.task

import org.gradle.api.DefaultTask

abstract class AbstractHermesTask : DefaultTask() {
    init {
        group = "Hermes"
    }
}