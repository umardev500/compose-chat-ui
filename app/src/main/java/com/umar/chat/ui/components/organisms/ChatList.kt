package com.umar.chat.ui.components.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.umar.chat.data.model.Chat
import com.umar.chat.ui.components.molecules.ChatItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatList(
    chats: List<Chat>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNavigate: (jid: String) -> Unit,
) {
    val listState = rememberLazyListState()


    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(state = listState) {
            if (chats.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                    )
                }
            }

            items(chats) { chat ->
                ChatItem(
                    chat = chat,
                    navigate = onNavigate
                )
            }
        }
    }
}