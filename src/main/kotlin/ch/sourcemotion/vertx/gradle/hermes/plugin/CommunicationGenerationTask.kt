package ch.sourcemotion.vertx.gradle.hermes.plugin

import ch.sourcemotion.vertx.gradle.hermes.plugin.task.AbstractHermesTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class CommunicationGenerationTask : AbstractHermesTask() {
    @get:Input
    abstract val outputPath: Property<String>
}