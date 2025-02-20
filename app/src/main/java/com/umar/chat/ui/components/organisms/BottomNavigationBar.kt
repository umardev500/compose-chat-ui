package com.umar.chat.ui.components.organisms

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.umar.chat.ui.components.atoms.IconType
import com.umar.chat.ui.components.atoms.MsIconRounded
import com.umar.chat.ui.components.atoms.MsIconRoundedFill
import com.umar.chat.ui.screens.Screens
import com.umar.chat.ui.theme.customColors

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

    val separatorColor = MaterialTheme.customColors.common.separator

    NavigationBar(
        modifier = Modifier
            .drawBehind {
                val borderSize: Dp = 1.dp
                drawLine(
                    color = separatorColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = borderSize.toPx()
                )
            },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.surface
    ) {
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
                                MsIconRoundedFill(
                                    icon.icon,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            } else {
                                MsIconRounded(icon.icon)
                            }
                        }
                    }
                },
                label = {
                    Text(text = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.customColors.navbar.indicator,
                )
            )
        }
    }
}