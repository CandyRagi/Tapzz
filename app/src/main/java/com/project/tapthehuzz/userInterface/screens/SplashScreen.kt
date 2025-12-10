package com.project.tapthehuzz.userInterface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.project.tapthehuzz.R
import com.project.tapthehuzz.data.repository.AuthRepository
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToNext: (String) -> Unit) {
    val authRepository = AuthRepository()

    LaunchedEffect(Unit) {
        delay(500) // 0.5 seconds delay
        if (authRepository.getCurrentUser() != null) {
            onNavigateToNext("home")
        } else {
            onNavigateToNext("sign_in")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tap_the_huzz_circle_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )
    }
}
