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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(onNavigateToNext: (String) -> Unit) {
    val authRepository = AuthRepository()

    LaunchedEffect(Unit) {
        delay(500) // 0.5 seconds delay
        if (authRepository.getCurrentUser() != null) {
            onNavigateToNext("home")
        } else {
            onNavigateToNext("signIn")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tap_the_huzz_circle_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            Text(
                text = "TAPZZ",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
