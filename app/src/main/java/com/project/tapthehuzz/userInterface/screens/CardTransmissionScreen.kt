package com.project.tapthehuzz.userInterface.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.tapthehuzz.data.model.Card
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
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        onDismiss()
    }

    // Back Handler
    BackHandler {
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
                .rotate(90f)
                .scale(1.2f) // Slightly larger
                .width(300.dp) // Fixed width for consistent look
                .height(180.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = Color(card.backgroundColor),
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Card Name (Top Left)
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    )

                    // Card Number (Top Right)
                    Text(
                        text = ".... ${card.cardNumber.takeLast(4)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )

                    // Profile Picture (Center)
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            if (card.imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = card.imageUrl,
                                    contentDescription = "Card Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Card Image",
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // User Name (Below PFP)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = username,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
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
