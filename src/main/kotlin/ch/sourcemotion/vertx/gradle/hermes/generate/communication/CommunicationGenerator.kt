package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import ch.sourcemotion.vertx.gradle.hermes.generate.Generator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import net.pwall.json.schema.JSONSchema
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

class CommunicationGenerator : Generator<CommunicationGeneratorConfiguration, Unit>() {

    private companion object {
        val communicationSchemaPath: Path =
            Paths.get(CommunicationGenerator::class.java.getResource("/schema/communication.json").toURI())
    }

    init {
        DatabindCodec.mapper().registerKotlinModule()
        DatabindCodec.prettyMapper().registerKotlinModule()
    }

    override fun generate(configuration: CommunicationGeneratorConfiguration) {
        val definitionFilePaths = findAllCommunicationDefinitions(configuration.inputDir.absolutePath)
        val definitions = definitionFilePaths.validateAllExists().validateForSchema().map { JsonObject(it.toFile().readText()) }.map { it.mapTo(
            CommunicationDefinition::class.java) }
        
    }

    private fun findAllCommunicationDefinitions(basePathValue: String): List<Path> =
        Files.list(Paths.get(basePathValue)).toList().flatMap {
            if (it.toFile().isDirectory) {
                findAllCommunicationDefinitions("$it")
            } else {
                it
            }
        }.filter { "$it".endsWith("json") || "$it".endsWith("yml") || "$it".endsWith("yaml") }

    private fun List<Path>.validateAllExists() : List<Path> {
        forEach {
            if (!Files.exists(it)) {
                throw CommunicationDefinitionException("Communication definition file not exists $it")
            }
        }
        return this
    }

    private fun List<Path>.validateForSchema() : List<Path> {
        val schema = JSONSchema.parse(communicationSchemaPath.toFile())
        forEach {
            if (!schema.validate(it.toFile().readText())) {
                throw CommunicationDefinitionException(
                    "Communication definition json \"$it\" is not valid according schema \"${
                        communicationSchemaPath.toFile().readText()
                    }\""
                )
            }
        }
        return this
    }
}

