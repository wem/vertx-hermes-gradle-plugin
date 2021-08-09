package ch.sourcemotion.vertx.gradle.hermes.generate.communication

data class CommunicationDefinition(
    val name: String,
    val address: String,
    val sendType: String,
    val replyType: String
)