package com.umar.chat.data.network

import android.util.Log
import com.umar.chat.BuildConfig
import com.umar.chat.data.model.PushChat
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatApiService {
    private val host = BuildConfig.API_HOST
    private val port = BuildConfig.API_PORT
    private val basePath = "/api/chat"

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    suspend fun listenForMessage(): Flow<PushChat> = flow {
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
                        val pushChat = PushChat.fromJson(text)
                        emit(pushChat)
                    }
                    else -> Unit
                }
            }

            val reason = closeReason.await()
            Log.d("ChatLog", "Websocket close reason: $reason")
        }
    }
}