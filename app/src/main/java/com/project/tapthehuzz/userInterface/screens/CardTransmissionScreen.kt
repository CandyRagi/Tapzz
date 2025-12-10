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
                .width(310.dp) // Increased width
                .height(190.dp) // Increased height
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = if (card.designId.isNotEmpty()) Color.White else Color(card.backgroundColor),
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background Design
                    if (card.designId == "design_one") {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.project.tapthehuzz.R.drawable.card_design_one),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Card Name (Top Left)
                    Surface(
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = card.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Card Number (Top Right)
                    Surface(
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = ".... ${card.cardNumber.takeLast(4)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Profile Picture (Center)
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            color = Color.Black
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
                                    modifier = Modifier.padding(12.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // User Name (Below PFP)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black
                        ) {
                            Text(
                                text = username,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
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
