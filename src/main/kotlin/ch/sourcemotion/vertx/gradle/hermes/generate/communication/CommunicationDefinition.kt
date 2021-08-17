package ch.sourcemotion.vertx.gradle.hermes.generate.communication

data class CommunicationDefinition(
    val name: String,
    val address: String,
    val sendType: SendType = SendType.SEND,
    val sendMessageType: String?,
    val replyMessageType: String?,
    val localOnly: Boolean = false
) {
    fun validate(): CommunicationDefinition {
        if ((sendType == SendType.PUBLISH || sendType == SendType.SEND) && replyMessageType != null) {
            throw CommunicationException("Communication definition \"$name\" of send type $sendType has defined a reply message type which is not supported")
        }
        return this
    }
}

enum class SendType {
    PUBLISH, SEND, REQUEST
}