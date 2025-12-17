package com.project.tapthehuzz.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.project.tapthehuzz.wear.data.repository.AuthRepository
import com.project.tapthehuzz.wear.presentation.theme.TapTheHuzzWearTheme

class WearApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TapTheHuzzWearTheme {
                WearAppContent()
            }
        }
    }
}

@Composable
fun WearAppContent() {
    val authRepository = remember { AuthRepository() }
    var isLoggedIn by remember { mutableStateOf(authRepository.getCurrentUser() != null) }

    if (isLoggedIn) {
        QuickAccessScreen(
            onLogout = { isLoggedIn = false }
        )
    } else {
        LoginScreen(
            onLoginSuccess = { isLoggedIn = true }
        )
    }
}
