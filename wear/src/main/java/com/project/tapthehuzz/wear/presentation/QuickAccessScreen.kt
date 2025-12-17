package com.project.tapthehuzz.wear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import coil.compose.AsyncImage
import com.project.tapthehuzz.wear.data.model.Card
import com.project.tapthehuzz.wear.data.model.User
import com.project.tapthehuzz.wear.data.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun QuickAccessScreen(
    onLogout: () -> Unit
) {
    val authRepository = remember { AuthRepository() }
    var user by remember { mutableStateOf<User?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    val listState = rememberScalingLazyListState()

    val quickAccessCards = remember(cards, user) {
        if (user != null) {
            cards.filter { it.id in user!!.quickAccessList }
        } else {
            emptyList()
        }
    }

    LaunchedEffect(Unit) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)

            userRef.addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    user = snapshot.toObject(User::class.java)
                }
            }

            userRef.collection("cards").addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    cards = snapshot.toObjects(Card::class.java)
                }
            }
        }
    }

    var selectedCard by remember { mutableStateOf<Card?>(null) }

    if (selectedCard != null) {
        WearCardTransmissionScreen(
            card = selectedCard!!,
            onDismiss = { selectedCard = null }
        )
    } else {
        Scaffold(
            timeText = { TimeText() },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(
                    top = 32.dp,
                    bottom = 32.dp,
                    start = 8.dp,
                    end = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                autoCentering = androidx.wear.compose.foundation.lazy.AutoCenteringParams(itemIndex = 0)
            ) {
                item {
                    Text(
                        text = "Quick Access",
                        style = MaterialTheme.typography.title2,
                        color = MaterialTheme.colors.primary
                    )
                }

                if (user != null) {
                    if (quickAccessCards.isEmpty()) {
                        item {
                            Text(
                                text = "No cards in Quick Access.\nAdd from phone app.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    } else {
                        items(quickAccessCards) { card ->
                            WearCardItem(
                                card = card,
                                onClick = { selectedCard = card }
                            )
                        }
                    }
                } else {
                    item {
                        Text("Loading...")
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                item {
                    androidx.wear.compose.material.Button(
                        onClick = {
                            authRepository.signOut()
                            onLogout()
                        },
                        colors = androidx.wear.compose.material.ButtonDefaults.secondaryButtonColors()
                    ) {
                        Text("Logout")
                    }
                }
            }
        }
    }
}

@Composable
fun WearCardItem(card: Card, onClick: () -> Unit) {
    TitleCard(
        onClick = onClick,
        title = { Text(card.name) },
        backgroundPainter = androidx.wear.compose.material.CardDefaults.imageWithScrimBackgroundPainter(
            backgroundImagePainter = coil.compose.rememberAsyncImagePainter(
                model = if (card.imageUrl.isNotEmpty()) card.imageUrl else null
            )
        ),
        contentColor = MaterialTheme.colors.onSurface,
        titleColor = MaterialTheme.colors.onSurface
    ) {
        if (card.imageUrl.isEmpty()) {
             Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(card.backgroundColor)),
                contentAlignment = Alignment.Center
            ) {
                 Text(
                     text = card.name.take(2).uppercase(),
                     style = MaterialTheme.typography.title1,
                     color = Color.Black // Assuming light background for simple color cards
                 )
            }
        }
    }
}
