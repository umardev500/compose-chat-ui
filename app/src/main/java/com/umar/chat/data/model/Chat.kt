package com.umar.chat.data.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

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
@SerialName("text") // ✅ Must match JSON field
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
    val message: Message? = null, // ✅ Will auto-detect type
    val isOnline: Boolean = false,
)

@Serializable
data class PushChatData(
    val isInitial: Boolean,
    val initialChat: Chat? = null,
    val message: Message? = null
)

@Serializable
data class PushChat(
    val mt: String,
    val data: PushChatData
) {
    companion object {
        // ✅ Register Message subclasses
        private val messageModule = SerializersModule {
            polymorphic(Message::class) {
                subclass(TextMessage::class, TextMessage.serializer())
            }
        }

        // ✅ Use "type" as class discriminator (without conflicting `type` property)
        val json = Json {
            ignoreUnknownKeys = true
            classDiscriminator = "type" // ✅ Automatically determines subclass
            serializersModule = messageModule
        }

        fun fromJson(json: String): PushChat {
            return Json.decodeFromString(json)
        }
    }
}

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)


fun main() {
    val jsonString = """
        {
            "mt": "text",
            "data": {
                "isInitial": false,
                "message": {
                        "type": "text",
                        "conversation": "Gasken",
                        "pushname": "Emma Davis",
                        "timestamp": 1738402565,
                        "metadata": {
                            "jid": "6281316057489@s.whatsapp.net",
                            "fromme": false,
                            "id": "A1B2C3D4E5F605"
                    }
                }
            }
        }
    """.trimIndent()

    // ✅ Deserialize correctly using the fixed JSON configuration
    val pushChat = PushChat.fromJson(jsonString)

    println("✅ Deserialized PushChat: $pushChat")
}
