package com.umar.chat.ui.components.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umar.chat.data.model.Chat
import com.umar.chat.ui.components.atoms.Avatar

@Composable
fun ChatItem(
    chat: Chat,
    navigate: (jid: String) -> Unit,
) {
    val profilePicUrl by remember { mutableStateOf<String?>(null) }
    val isUnread = chat.unread > 0

    Row(
        modifier = Modifier
            .clickable {
                navigate(chat.jid)
            }
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Avatar(imageUrl = profilePicUrl, isOnline = chat.isOnline)
        Column(
            modifier = Modifier
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // Chat Name and Time
            ChatNameWithTime(
                metadata = chat.message?.metadata,
                isUnread
            )

            // Bottom
            ChatMessageDetails(
                chat.message?.getContent(),
                count = chat.unread,
            )
        }

    }
}