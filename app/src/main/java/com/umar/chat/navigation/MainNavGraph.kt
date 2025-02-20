package com.umar.chat.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.umar.chat.ui.screens.ChatScreen
import com.umar.chat.ui.screens.MessagingScreen
import com.umar.chat.ui.screens.NotificationScreen
import com.umar.chat.ui.screens.Screens
import com.umar.chat.ui.screens.SettingScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val navigationActions = remember {
        NavigationActions(
            navigateToMessaging = { jid ->
                navController.navigate(Screens.Messaging.createRoute(jid))
            },
            backStack = {
                navController.popBackStack()
            }
        )
    }

    NavigationLocalComp.Provide(
        navigationActions = navigationActions
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = Screens.Chat.route
        ) {
            composable(
                route = Screens.Chat.route,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                ChatScreen()
            }

            composable(
                route = Screens.Queue.route,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                ChatScreen()
            }
            composable(
                route = Screens.Notifications.route,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                NotificationScreen()
            }
            composable(
                route = Screens.Settings.route,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                SettingScreen()
            }
            composable(
                route = Screens.Messaging.route,
                enterTransition = { SlideTransition.enter },
                exitTransition = { SlideTransition.exit },
                popEnterTransition = { SlideTransition.popEnter },
                popExitTransition = { SlideTransition.popExit }
            ) {
                MessagingScreen()
            }

        }
    }

}