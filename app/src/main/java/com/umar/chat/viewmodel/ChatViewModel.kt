package com.umar.chat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umar.chat.data.model.Chat
import com.umar.chat.data.network.ChatApiService
import com.umar.chat.data.repository.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatApiService: ChatApiService,
    private val userManager: UserManager
) : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    init {
        listenForMessage()
        fetchChats()
    }

    private fun fetchChats() {
        viewModelScope.launch {
            val csid = userManager.getCsId()
            _chats.value = chatApiService.fetchChats(csid)
        }
    }

    private fun listenForMessage() {
        Log.d("ChatLog", "Listening for messages...")

        viewModelScope.launch {
            chatApiService.listenForMessage()
                .catch {
                    Log.d("ChatLog", "Error: $it")
                }
                .collect { pushChat ->
                    val newChat = pushChat.data.initialChat
                    val newMesage = pushChat.data.message

                    _chats.update { chats ->
                        when {
                            newChat != null -> listOf(newChat) + chats
                            newMesage != null -> chats.map { chat ->
                                if (chat.jid == newMesage.getJid()) {
                                    chat.copy(
                                        message = pushChat.data.message,
                                        unread = chat.unread + 1,
                                    )
                                } else {
                                    chat
                                }

                            }

                            else -> chats
                        }
                    }

                }
        }
    }
}