package com.project.tapthehuzz.userInterface.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale

import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.unit.dp

import com.project.tapthehuzz.data.model.Card
import com.project.tapthehuzz.utils.CardNfcManager
import kotlinx.coroutines.delay

@Composable
fun CardTransmissionScreen(
    card: Card,
    username: String,
    onDismiss: () -> Unit
) {
    var timeLeft by remember { mutableIntStateOf(60) }

    // Timer
    LaunchedEffect(Unit) {
        android.util.Log.e("HCE_DEBUG", "Screen Opened. Setting URL: ${card.link}")
        CardNfcManager.currentCardUrl = card.link
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        CardNfcManager.currentCardUrl = null
        onDismiss()
    }

    // Back Handler
    BackHandler {
        CardNfcManager.currentCardUrl = null
        onDismiss()
    }

    // Animation for Ripple Effect
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // Rotation Animation
    val rotation = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 90f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Ripple Effect
        Box(
            modifier = Modifier
                .size(300.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
        )

        // Rotated Card
        Box(
            modifier = Modifier
                .rotate(rotation.value)
                .scale(1.2f) // Slightly larger
                .width(340.dp) // Adjusted width for CardItem aspect ratio
                .height(220.dp) // Adjusted height for CardItem aspect ratio
        ) {
            com.project.tapthehuzz.userInterface.components.CardItem(
                card = card,
                username = username,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Timer at Bottom
        Text(
            text = "Closing in ${timeLeft}s",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        )
    }
}
