package com.umar.chat.ui.components.organisms

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import com.umar.chat.ui.components.atoms.IconType
import com.umar.chat.ui.components.atoms.MsIconRounded
import com.umar.chat.ui.components.atoms.MsIconRoundedFill
import com.umar.chat.ui.screens.Screens

sealed class NavigationIcon {
    data class VectorIcon(val icon: ImageVector) : NavigationIcon()
    data class CustomIcon(val icon: IconType) : NavigationIcon()
}

data class NavigationBarItem(val route: String, val icon: NavigationIcon, val fillSelected: Boolean = false, val label: String)

@Composable
fun BottomNavigationBar() {
    val items = listOf(
        NavigationBarItem(
            route = Screens.Chat.route,
            icon = NavigationIcon.CustomIcon(icon = IconType.HOME),
            fillSelected = true,
            label = "Home"
        ),
        NavigationBarItem(
            route = Screens.Chat.route,
            icon = NavigationIcon.CustomIcon(icon = IconType.SCHEDULE),
            fillSelected = true,
            label = "Queue"
        ),
        NavigationBarItem(
            route = Screens.Chat.route,
            icon = NavigationIcon.CustomIcon(icon = IconType.NOTIFICATONS),
            fillSelected = true,
            label = "Notifications"
        ),
        NavigationBarItem(
            route = Screens.Chat.route,
            icon = NavigationIcon.CustomIcon(icon = IconType.SETTINGS),
            fillSelected = true,
            label = "Settings"
        )
    )

    val selectedIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex.intValue == index,
                onClick = {
                    selectedIndex.intValue = index
                },
                icon = {
                    when (val icon = item.icon) {
                        is NavigationIcon.VectorIcon -> {}
                        is NavigationIcon.CustomIcon -> {
                            if (item.fillSelected && selectedIndex.intValue == index) {
                                MsIconRoundedFill(icon.icon)
                            } else {
                                MsIconRounded(icon.icon)
                            }
                        }
                    }
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}