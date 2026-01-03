package com.project.tapthehuzz

import android.content.ComponentName
import android.nfc.NfcAdapter
import android.nfc.cardemulation.CardEmulation
import android.os.Bundle
import android.util.Log
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
import com.project.tapthehuzz.services.MyHostApduService
import com.project.tapthehuzz.userInterface.screens.HistoryScreen
import com.project.tapthehuzz.userInterface.screens.HomeScreen
import com.project.tapthehuzz.userInterface.screens.ProfileScreen
import com.project.tapthehuzz.userInterface.screens.SignInScreen
import com.project.tapthehuzz.userInterface.screens.SignUpScreen
import com.project.tapthehuzz.userInterface.theme.TapTheHuzzTheme

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var cardEmulation: CardEmulation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        cardEmulation = nfcAdapter?.let { CardEmulation.getInstance(it) }

        setContent {
            TapTheHuzzTheme {
                MainApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Set our service as the preferred service when app is in foreground
        // This prevents the "Select an app" dialog
        cardEmulation?.setPreferredService(
            this,
            ComponentName(this, MyHostApduService::class.java)
        )
        Log.d("MainActivity", "Set preferred HCE service")
    }

    override fun onPause() {
        super.onPause()
        // Unset preferred service when app goes to background
        cardEmulation?.unsetPreferredService(this)
        Log.d("MainActivity", "Unset preferred HCE service")
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
        val startDestination = "splash"

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                androidx.compose.animation.slideInHorizontally(initialOffsetX = { 1000 }) + androidx.compose.animation.fadeIn()
            },
            exitTransition = {
                androidx.compose.animation.slideOutHorizontally(targetOffsetX = { -1000 }) + androidx.compose.animation.fadeOut()
            },
            popEnterTransition = {
                androidx.compose.animation.slideInHorizontally(initialOffsetX = { -1000 }) + androidx.compose.animation.fadeIn()
            },
            popExitTransition = {
                androidx.compose.animation.slideOutHorizontally(targetOffsetX = { 1000 }) + androidx.compose.animation.fadeOut()
            }
        ) {
            composable("splash") {
                com.project.tapthehuzz.userInterface.screens.SplashScreen(
                    onNavigateToNext = { route ->
                        navController.navigate(route) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
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
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onSignOut = {
                        navController.navigate("signIn") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}