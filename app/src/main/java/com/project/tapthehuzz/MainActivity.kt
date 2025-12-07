package com.project.tapthehuzz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.tapthehuzz.userInterface.screens.HistoryScreen
import com.project.tapthehuzz.userInterface.screens.HomeScreen
import com.project.tapthehuzz.userInterface.screens.ProfileScreen
import com.project.tapthehuzz.userInterface.screens.ProfileScreen
import com.project.tapthehuzz.userInterface.screens.SignInScreen
import com.project.tapthehuzz.userInterface.screens.SignUpScreen
import com.project.tapthehuzz.userInterface.theme.TapTheHuzzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TapTheHuzzTheme {
                MainApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object History : Screen("history", "History", Icons.Filled.History)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    Scaffold(
    ) { innerPadding ->
        val authRepository = com.project.tapthehuzz.data.repository.AuthRepository()
        val startDestination = if (authRepository.getCurrentUser() != null) Screen.Home.route else "signIn"

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("signIn") {
                SignInScreen(
                    onNavigateToSignUp = { navController.navigate("signUp") },
                    onSignInSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("signIn") { inclusive = true }
                        }
                    }
                )
            }
            composable("signUp") {
                SignUpScreen(
                    onNavigateToSignIn = { navController.navigate("signIn") },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("signIn") { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}