package com.umar.chat.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.umar.chat.navigation.LocalNavigationActions
import com.umar.chat.navigation.NavigationActions
import com.umar.chat.ui.components.organisms.ChatHeader
import com.umar.chat.ui.components.organisms.ChatList
import com.umar.chat.viewmodel.ChatViewModel

data class ChatScreenActions(
    val getPicture: suspend (jid: String) -> String
)

val LocalChatScreenActions = staticCompositionLocalOf<ChatScreenActions> {
    error("No navigation actions provided")
}

@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    val navigationActions: NavigationActions = LocalNavigationActions.current
    val uiState by viewModel.uiState.collectAsState()

    // Function to refresh chat data
    fun handleRefresh() {
    }

    // Function to handle navigation to messaging screen
    fun handleNavigate(jid: String) {
        navigationActions.navigateToMessaging(jid)
    }

    val actions = remember {
        ChatScreenActions(
            getPicture = { jid ->
                viewModel.getProfilePic(jid)
            }
        )
    }

    CompositionLocalProvider(LocalChatScreenActions provides actions) {
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
                    chats = uiState.chats,
                    isRefreshing = uiState.isLoading,
                    onRefresh = ::handleRefresh,
                    onNavigate = ::handleNavigate,
                )
            }
        }
    }
}