package com.umar.chat.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.umar.chat.navigation.LocalNavigationActions
import com.umar.chat.navigation.NavigationActions
import com.umar.chat.ui.components.organisms.ChatHeader
import com.umar.chat.ui.components.organisms.ChatList
import com.umar.chat.viewmodel.ChatViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    val navigationActions: NavigationActions = LocalNavigationActions.current
    val chats by viewModel.chats.collectAsState()

    Log.d("ChatLog", "Chats: $chats")

    // Function to refresh chat data
    fun handleRefresh() {
    }

    // Function to handle navigation to messaging screen
    fun handleNavigate(jid: String) {
        navigationActions.navigateToMessaging(jid)
    }

    Scaffold(
        topBar = {
            ChatHeader()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            ChatList(
                chats = chats,
                isRefreshing = true,
                onRefresh = ::handleRefresh,
                onNavigate = ::handleNavigate,
            )
        }
    }
}