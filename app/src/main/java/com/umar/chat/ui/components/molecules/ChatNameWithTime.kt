package com.umar.chat.ui.components.molecules

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.umar.chat.data.model.Metadata
import com.umar.chat.ui.theme.Gray600
import com.umar.chat.ui.theme.Gray700
import com.umar.chat.ui.theme.Green600
import com.umar.chat.utils.formatEpochTime

@Composable
fun ChatNameWithTime(
    metadata: Metadata?,
    isUnread: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Chat Name
        Text(
            text = metadata?.pushname ?: "",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray700
            ),
            modifier = Modifier.weight(1f)
        )

        // Time
        Text(
            text = formatEpochTime(metadata?.timestamp ?: 0L),
            color = if (isUnread) Green600 else Gray600,
            fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}