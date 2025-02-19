package com.umar.chat.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
data class Chat(
    val jid: String,
    val csid: String,
    val status: String,
    val unread: Int,
    val message: Message? = null,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false,
)


@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

@Serializable
sealed class Content

@Serializable
data class Metadata(
    val type: String,
    val jid: String,
    val fromme: Boolean = false,
    val id: String,
    val pushname: String,
    val timestamp: Long = 0L
)

@Serializable
data class TextMessage(
    val conversation: String,
) : Content()

@Serializable
data class Message(
    @SerialName("message") val content: JsonElement? = null,
    val metadata: Metadata,
    val unread: Boolean
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromJsonEl(jsonElement: JsonElement): Message {
            return json.decodeFromJsonElement(serializer(), jsonElement)
        }
    }

    fun getContent(): Content? {
        return when (metadata.type) {
            "TEXT" -> {
                return json.decodeFromJsonElement(TextMessage.serializer(), content!!)
            }

            else -> null
        }
    }
}

@Serializable
data class MessageBroadcastResponse(
    val isInitial: Boolean,
    val data: JsonElement? = null
) : BroadcastData() {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromJson(jsonString: String): MessageBroadcastResponse {
            return json.decodeFromString(jsonString)
        }
    }

    fun getMessage(): Message? {
        return data?.let {
            Message.fromJsonEl(it)
        }
    }

    fun getInitialChat(): Chat? {
        return getMessage()?.let {
            Chat(
                jid = it.metadata.jid,
                csid = "",
                status = "queued",
                unread = 1,
                message = it,
                isOnline = false
            )
        }
    }
}

@Serializable
sealed class BroadcastData

@Serializable
data class TypingData(
    val jid: String,
    val typing: Boolean? = false
) : BroadcastData()

@Serializable
data class OnlineData(
    val jid: String,
    val online: Boolean = false
) : BroadcastData()

@Serializable
data class WebsocketBroadcast(
    val type: String,
    val data: JsonElement? = null
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromJson(jsonString: String): WebsocketBroadcast {
            return json.decodeFromString(jsonString)
        }
    }

    fun getData(): BroadcastData? {
        return when (type) {
            "typing" -> {
                json.decodeFromJsonElement(TypingData.serializer(), data!!)
            }

            "online" -> {
                json.decodeFromJsonElement(OnlineData.serializer(), data!!)
            }

            "message" -> {
                json.decodeFromJsonElement(MessageBroadcastResponse.serializer(), data!!)
            }

            else -> null
        }
    }
}


fun main() {
    val jsonString = """
        {
          "message": {
            "conversation": "world"
          },
          "metadata": {
            "jid": "6283142765573@s.whatsapp.net",
            "fromme": false,
            "id": "3EB0B584850746014C0107",
            "pushname": "Umar Schweinsteiger",
            "timestamp": 1739671838,
            "type": "TEXT"
          },
          "unread": true
        }
    """.trimIndent()

    val broadcastString = """
        {
          "isInitial": false,
          "data": {
            "message": {
              "conversation": "world"
            },
            "metadata": {
              "jid": "6283142765573@s.whatsapp.net",
              "fromme": false,
              "id": "3EB0B584850746014C0107",
              "pushname": "Umar Schweinsteiger",
              "timestamp": 1739671838,
              "type": "TEXT"
            },
            "unread": true
          }
        }
    """.trimIndent()


    val pushChat = MessageBroadcastResponse.fromJson(broadcastString)
    println(pushChat)
}