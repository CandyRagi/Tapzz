package com.project.tapthehuzz.wear.presentation

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.project.tapthehuzz.wear.data.model.Card
import com.project.tapthehuzz.wear.utils.WearCardNfcManager
import kotlinx.coroutines.delay

@Composable
fun WearCardTransmissionScreen(
    card: Card,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var timeLeft by remember { mutableStateOf(60) }
    var nfcStatus by remember { mutableStateOf("Initializing...") }

    // Initialize HCE and timer
    LaunchedEffect(Unit) {
        if (activity == null) {
            nfcStatus = "Error: Not in Activity context"
            return@LaunchedEffect
        }

        // Check if NFC is available
        if (!WearCardNfcManager.isNfcAvailable(context)) {
            nfcStatus = "NFC not available"
            delay(2000)
            onDismiss()
            return@LaunchedEffect
        }

        // Set the card URL and enable HCE
        WearCardNfcManager.currentCardUrl = card.link
        WearCardNfcManager.enableCardEmulation(activity)
        nfcStatus = "Ready - Tap to phone"

        // Countdown timer
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }

        // Cleanup and dismiss
        WearCardNfcManager.disableCardEmulation(activity)
        WearCardNfcManager.currentCardUrl = null
        onDismiss()
    }

    // Cleanup on dispose (user swipes back)
    DisposableEffect(Unit) {
        onDispose {
            activity?.let {
                WearCardNfcManager.disableCardEmulation(it)
                WearCardNfcManager.currentCardUrl = null
            }
        }
    }

    // Animation for Ripple Effect
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        // Ripple Effect
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary.copy(alpha = alpha))
        )

        // Central content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = nfcStatus,
                style = MaterialTheme.typography.caption1,
                color = MaterialTheme.colors.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = card.name,
                style = MaterialTheme.typography.title3,
                color = MaterialTheme.colors.primary
            )

            if (nfcStatus == "Ready - Tap to phone") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hold watch to phone",
                    style = MaterialTheme.typography.caption2,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        // Timer at Bottom
        if (timeLeft > 0 && nfcStatus == "Ready - Tap to phone") {
            Text(
                text = "${timeLeft}s",
                style = MaterialTheme.typography.body1,
                color = if (timeLeft <= 10) {
                    Color.Red
                } else {
                    MaterialTheme.colors.onBackground
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}a