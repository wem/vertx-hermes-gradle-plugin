package ch.sourcemotion.vertx.gradle.hermes.generate.communication

import net.pwall.json.schema.JSONSchema
import java.io.File
import java.util.*

object CommunicationJsonValidator {
    private const val SCHEMA_PATH = "/schema/communication.json"

    fun validate(json: String) {
        val schema = loadSchema()
        val output = schema.validateBasic(json)
        if (!output.valid) {
            val errorLines = output.errors?.map { "${it.error} > ${it.instanceLocation}" } ?: emptyList()
            val errorMsg = errorLines.joinToString(", \n")
            throw JsonValidationException(errorMsg)
        }
    }

    private fun loadSchema() : JSONSchema {
        val schemaIS = CommunicationJsonValidator::class.java.getResourceAsStream(SCHEMA_PATH)
            ?: throw CommunicationException("Failed to load communication schema filesystem resource from $SCHEMA_PATH")
        val schemaFile = File.createTempFile("hermes-communication-schema-${UUID.randomUUID()}", "json")
        schemaFile.outputStream().use { os -> os.write(schemaIS.use { ins -> ins.readAllBytes() }) }
        return JSONSchema.parse(schemaFile)
    }
}