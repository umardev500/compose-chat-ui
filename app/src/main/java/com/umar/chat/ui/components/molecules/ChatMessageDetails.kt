package com.umar.chat.ui.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.umar.chat.data.model.Content
import com.umar.chat.data.model.TextMessage
import com.umar.chat.ui.components.atoms.MessageCount
import com.umar.chat.ui.theme.Gray600
import com.umar.chat.ui.theme.Gray800
import com.umar.chat.ui.theme.Green600

@Composable
fun ChatMessageDetails(message: Content?, count: Int, isTyping: Boolean = false) {
    val isUnread = count > 0

    var higlightMessage = ""
    when (message) {
        is TextMessage -> {
            higlightMessage = message.conversation
        }

        else -> Unit
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Higlight message
        if (isTyping) {
            Text(
                text = "typing...",
                color = Green600,
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        } else {
            Text(
                text = higlightMessage,
                fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal,
                color = if (isUnread) Gray800 else Gray600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        if (count > 0) {
            MessageCount(count = count)
        }
    }
}