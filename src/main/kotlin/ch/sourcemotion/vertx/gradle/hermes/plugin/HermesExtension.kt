package ch.sourcemotion.vertx.gradle.hermes.plugin

import ch.sourcemotion.vertx.gradle.hermes.generate.*
import ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec.MessageCodecGenerationTask.Companion.DEFAULT_MESSAGE_CODEC_FILE_NAME
import ch.sourcemotion.vertx.gradle.hermes.generate.messagecodec.MessageCodecGenerationTask.Companion.DEFAULT_MESSAGE_CODEC_NAME_SUPPLIER
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.util.function.Function
import javax.inject.Inject

abstract class HermesExtension(@Inject private val project: Project) {

    @get:Nested
    abstract val dto: DtoConfiguration

    @get:Nested
    abstract val codec: MessageCodecConfiguration

    @get:Nested
    abstract val communication: CommunicationConfiguration

    @get:Input
    abstract val sourceSet: Property<SourceSet>

    internal fun applyFixDefaultValues(): HermesExtension {
        dto.enable.convention(true)

        codec.enable.convention(true)
        codec.messageCodecsFileName.convention(DEFAULT_MESSAGE_CODEC_FILE_NAME)
        codec.messageCodecNameSupplier.convention(DEFAULT_MESSAGE_CODEC_NAME_SUPPLIER)

        communication.enable.convention(true)

        return this
    }

    fun dto(action: Action<DtoConfiguration>) {
        action.execute(dto)
    }

    fun codec(action: Action<MessageCodecConfiguration>) {
        action.execute(codec)
    }

    fun communication(action: Action<CommunicationConfiguration>) {
        action.execute(communication)
    }

    internal fun evaluateSourceSet(): SourceSet = if (sourceSet.isPresent) {
        sourceSet.get()
    } else {
        val sourceSetContainer = project.extensions.getByType(SourceSetContainer::class.java)
        sourceSetContainer.first { it.name == "main" }
    }
}

abstract class BaseConfiguration : DefinesPackageName, DefinesOutputDir {
    @get:Input
    abstract override val outputDir: DirectoryProperty

    @get:Input
    abstract val enable: Property<Boolean>

    @get:Input
    abstract override val packageName: Property<String>
}

abstract class BaseConfigurationWithInputDir : BaseConfiguration(), DefinesInputDir {
    @get:Input
    abstract override val inputDir: DirectoryProperty
}

abstract class DtoConfiguration : BaseConfigurationWithInputDir()

abstract class MessageCodecConfiguration : BaseConfiguration(), DefinesClassesInfo, DefinesMessageCodecsFileName,
    DefinesMessageCodecNameSupplier {
    @get:Input
    abstract override val classesInfo: ListProperty<DtoClassInfo>

    @get:Input
    abstract override val messageCodecsFileName: Property<String>

    @get:Internal
    abstract override val messageCodecNameSupplier: Property<Function<DtoClassInfo, String>>
}

abstract class CommunicationConfiguration : BaseConfigurationWithInputDir(), DefinesGenerateSender,
    DefinesGenerateConsumer {
    @get:Input
    abstract override val generateSender: Property<Boolean>

    @get:Input
    abstract override val generateConsumer: Property<Boolean>
}