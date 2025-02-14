package com.umar.chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object NavigationLocalComp {
    @Composable
    fun Provide(
        navigationActions: NavigationActions,
        content: @Composable () -> Unit
    ) {
        CompositionLocalProvider(
            LocalNavigationActions provides navigationActions,
        ) {
            content()
        }
    }
}