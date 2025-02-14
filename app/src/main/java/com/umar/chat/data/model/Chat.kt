package com.umar.chat.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Utility function to parse message dynamically.
 */
fun parseMessage(mt: String, jsonElement: JsonElement?): Message? {
    return jsonElement?.let { msgJson ->
        when (mt) {
            "text" -> PushChat.json.decodeFromJsonElement(TextMessage.serializer(), msgJson)
            else -> null
        }
    }
}

@Serializable
sealed class Message {
    abstract val timestamp: Long
    abstract val metadata: Metadata

    abstract fun getJid(): String
}

@Serializable
data class Metadata(
    val jid: String,
    val fromme: Boolean,
    val id: String
)

@Serializable
data class TextMessage(
    val conversation: String,
    val pushname: String,
    override val timestamp: Long,
    override val metadata: Metadata
) : Message() {
    override fun getJid(): String = metadata.jid
}

@Serializable
data class Chat(
    val jid: String,
    val csid: String,
    val status: String,
    val unread: Int,
    val message: JsonElement? = null, // Automatically deserializes to the correct type
    // extended field
    val isOnline: Boolean = false,
    val mt: String = ""
) {
    /**
     * Deserialize the `message` field dynamically.
     */
    fun parseMessage(mt: String): Message? = parseMessage(mt, message)
}


@Serializable
data class PushChatData(
    val isInitial: Boolean,
    val initialChat: Chat? = null, // Nullable like `omitempty`
    val message: JsonElement? = null // Dynamic message field
) {
    /**
     * Universal method to parse a message based on `mt`.
     */
    fun parseMessage(mt: String): Message? = parseMessage(mt, message)
}

@Serializable
data class PushChat(
    val mt: String,
    val data: PushChatData
) {
    companion object {
        val json = Json { ignoreUnknownKeys = true } // Global JSON instance

        fun fromJson(jsonString: String): PushChat {
            return json.decodeFromString(jsonString)
        }
    }
}


fun main() {
    val jsonString = """
        {
            "mt": "text",
            "data": {
                "isInitial": false,
                "message": {
                    "conversation": "How do you do?",
                    "metadata": {
                        "fromme": false,
                        "id": "A1B2C3D4E5F605",
                        "jid": "6285773027798@s.whatsapp.net"
                    },
                    "pushname": "Emma Davis",
                    "timestamp": 1738402565
                }
            }
        }
    """.trimIndent()


    val jsonStringInitial = """
        {
            "mt": "text",
            "data": {
                "isInitial": false,
                "initialChat": {
                    "jid": "6281316057489@s.whatsapp.net",
                    "csid": "xyz",
                    "status": "queued",
                    "unread": 1,
                    "message": {
                        "conversation": "How do you do?",
                        "metadata": {
                            "fromme": false,
                            "id": "A1B2C3D4E5F605",
                            "jid": "6285773027798@s.whatsapp.net"
                        },
                        "pushname": "Emma Davis",
                        "timestamp": 1738402565
                    }
                }
            }
        }
    """.trimIndent()

    // Deserialize PushChat object
    val pushChat = PushChat.fromJson(jsonStringInitial)

    // Deserialize message based on `mt`
    val message = pushChat.data.initialChat?.parseMessage(pushChat.mt)

    println(message)

}
