package com.umar.chat.ui.components.organisms

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.umar.chat.ui.components.atoms.IconType
import com.umar.chat.ui.components.atoms.MsIconRounded
import com.umar.chat.ui.components.atoms.MsIconRoundedFill
import com.umar.chat.ui.screens.Screens

sealed class NavigationIcon {
    data class VectorIcon(val icon: ImageVector) : NavigationIcon()
    data class CustomIcon(val icon: IconType) : NavigationIcon()
}

data class NavigationBarItem(
    val route: String,
    val icon: NavigationIcon,
    val fillSelected: Boolean = false,
    val label: String
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationBarItem(
            route = Screens.Chat.route,
            icon = NavigationIcon.CustomIcon(icon = IconType.HOME),
            fillSelected = true,
            label = "Home"
        ),
        NavigationBarItem(
            route = Screens.Queue.route,
            icon = NavigationIcon.CustomIcon(icon = IconType.SCHEDULE),
            fillSelected = true,
            label = "Queue"
        ),
        NavigationBarItem(
            route = Screens.Notifications.route,
            icon = NavigationIcon.CustomIcon(icon = IconType.NOTIFICATONS),
            fillSelected = true,
            label = "Notifications"
        ),
        NavigationBarItem(
            route = Screens.Settings.route,
            icon = NavigationIcon.CustomIcon(icon = IconType.SETTINGS),
            fillSelected = true,
            label = "Settings"
        )
    )

    val selectedIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    // Observe the current back stack entry
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Prevent multiple copies of the same destination
                            launchSingleTop = true

                            // Pop up to the start destination but save state
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }

                            // Restore state when re-selecting a tab
                            restoreState = true
                        }
                    }
                },
                icon = {
                    when (val icon = item.icon) {
                        is NavigationIcon.VectorIcon -> {}
                        is NavigationIcon.CustomIcon -> {
                            if (item.fillSelected && currentRoute == item.route) {
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