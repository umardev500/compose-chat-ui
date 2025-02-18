package com.umar.chat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umar.chat.data.model.Chat
import com.umar.chat.data.network.ChatApiService
import com.umar.chat.data.repository.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private val profilePics = mutableMapOf<String, String>()
    private var listenJob: Job? = null

    init {
        refresh()
    }

    private fun fetchChats() {
        viewModelScope.launch {
            val csid = userManager.getCsId()

            // ✅ Batch updates: Set loading state and update chats in a single emit
            _uiState.update { it.copy(isLoading = true) }

            val chatList = runCatching { chatApiService.fetchChats(csid) }
                .getOrElse {
                    emptyList()
                }

            _uiState.update { it.copy(isLoading = false, chats = chatList) }
        }
    }

    suspend fun getProfilePic(jid: String): String {
        return profilePics.getOrPut(jid) {
            val provilePicUrl = runCatching {
                chatApiService.getProfilePic(jid)
            }.getOrElse {
                null
            }

            provilePicUrl ?: ""
        }
    }

    private fun listenForMessage() {
        Log.d("ChatLog", "Listening for messages...")

        listenJob?.cancel()

        listenJob = viewModelScope.launch {
            chatApiService.listenForMessage()
                .catch {
                    Log.d("ChatLog", "Error: $it")
                }
                .collect { msg ->
                    val isInitial = msg.isInitial
                    val newMesage = msg.getMessage()
                    val metadata = newMesage?.metadata

                    _uiState.update { currentState ->
                        val updatedChats = when {
                            isInitial -> {
                                msg.getInitialChat()?.let {
                                    listOf(it)
                                } ?: currentState.chats
                            }

                            newMesage != null -> {
                                currentState.chats.map { chat ->
                                    if (chat.jid == metadata?.jid) {
                                        chat.copy(
                                            message = newMesage,
                                            unread = chat.unread + 1,
                                        )
                                    } else {
                                        // If the chat is not the target chat, return it unchanged
                                        chat
                                    }
                                }.sortedByDescending { chat ->
                                    if (chat.jid == metadata?.jid) Long.MAX_VALUE else chat.message?.metadata?.timestamp
                                }
                            }

                            else -> currentState.chats
                        }

                        currentState.copy(chats = updatedChats)
                    }

                }
        }
    }

    fun refresh() {
        fetchChats()
        listenForMessage()
    }
}

data class ChatUiState(
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = false
)