package com.umar.chat.data.network

import android.util.Log
import com.umar.chat.BuildConfig
import com.umar.chat.data.model.ApiResponse
import com.umar.chat.data.model.BroadcastData
import com.umar.chat.data.model.Chat
import com.umar.chat.data.model.WebsocketBroadcast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Singleton

@Singleton
class ChatApiService {
    private val host = BuildConfig.API_HOST
    private val port = BuildConfig.API_PORT
    private val basePath = "/api/chat"
    private val baseURL = "http://$host:$port$basePath"

    private val client = HttpClient(CIO) {
        install(WebSockets)
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun fetchChats(csid: String): List<Chat> {
        val response: ApiResponse<List<Chat>> = client.get("$baseURL?csid=$csid&token=xyz").body()
        return response.data
    }

    suspend fun getProfilePic(jid: String): String? {
        return try {
            val response: ApiResponse<String> =
                client.get("$baseURL/wa-picture/$jid?token=xyz").body()
            if (response.success) {
                response.data
            } else {
                throw Exception("Failed to fetch profile picture $jid")
            }
        } catch (e: Exception) {
            null
        }
    }

    fun listenForMessage(): Flow<BroadcastData> = flow {
        client.webSocket(
            method = HttpMethod.Get,
            host = host,
            port = port,
            path = "$basePath/ws?token=xyz"
        ) {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        val broadcastData = WebsocketBroadcast.fromJson(text)
                        val data = broadcastData.getData()
                        data?.let { emit(it) }
                    }

                    else -> Unit
                }
            }

            val reason = closeReason.await()
            Log.d("ChatLog", "Websocket close reason: $reason")
        }
    }

    suspend fun updateUnread(jid: String, csid: String) {
        try {
            client.patch("$baseURL/update-unread?jid=$jid&csid=$csid")
        } catch (e: Exception) {
            throw e
        }
    }
}