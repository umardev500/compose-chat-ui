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
                .collect { msg ->
                    val isInitial = msg.isInitial
                    val newMesage = msg.getMessage()
                    val metadata = newMesage?.metadata

                    _chats.update { chats ->
                        when {
                            // If a new chat is created, add it to the top of the list
                            isInitial -> {
                                msg.getInitialChat()?.let {
                                    listOf(it) + chats
                                } ?: chats
                            }

                            // If there's a new message, find and update the corresponding chat, then move it to the top
                            newMesage != null -> {
                                val updatedChats = chats.map { chat ->
                                    if (chat.jid == metadata?.jid) {
                                        chat.copy(
                                            message = newMesage,
                                            unread = chat.unread + 1,
                                        )
                                    } else {
                                        // If the chat is not the target chat, return it unchanged
                                        chat
                                    }
                                }
                                // Move the updated chat to the top
                                updatedChats.sortedByDescending { chat ->
                                    if (chat.jid == metadata?.jid) Long.MAX_VALUE else chat.message?.metadata?.timestamp
                                }
                            }

                            // If no new chat or message, return the current chat list as is
                            else -> chats
                        }
                    }
                }
        }
    }

}