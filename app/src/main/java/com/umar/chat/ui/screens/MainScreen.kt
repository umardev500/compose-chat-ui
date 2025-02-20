package com.umar.chat.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.umar.chat.navigation.MainNavGraph
import com.umar.chat.ui.components.organisms.BottomNavigationBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        MainNavGraph(
            Modifier
                .padding(padding)
                .consumeWindowInsets(padding), // Prevfent double padding
            navController
        )
    }
}