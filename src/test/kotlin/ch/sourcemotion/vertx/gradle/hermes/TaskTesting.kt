package ch.sourcemotion.vertx.gradle.hermes

import ch.sourcemotion.vertx.gradle.hermes.generate.dto.DtoGenerationTask
import ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec.MessageCodecGenerationTask
import io.kotest.matchers.types.shouldBeInstanceOf
import org.gradle.api.Project

fun Project.getDtoGenerationTask(block: (DtoGenerationTask) -> Unit) =
    tasks.getByName(DtoGenerationTask.NAME).shouldBeInstanceOf<DtoGenerationTask>().let(block)

fun Project.getMessageCodecGenerationTask(block: (MessageCodecGenerationTask) -> Unit) =
    tasks.getByName(MessageCodecGenerationTask.NAME).shouldBeInstanceOf<MessageCodecGenerationTask>().let(block)