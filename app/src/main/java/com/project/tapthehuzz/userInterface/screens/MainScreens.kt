package com.project.tapthehuzz.userInterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.project.tapthehuzz.data.model.Card
import com.project.tapthehuzz.userInterface.components.CreateCardDialog
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.project.tapthehuzz.data.model.User
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.project.tapthehuzz.data.repository.AuthRepository

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onProfileClick: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Cards") }
    var userPfp by remember { mutableStateOf("") }
    var user by remember { mutableStateOf<User?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    var showCreateCardDialog by remember { mutableStateOf(false) }
    var activeCard by remember { mutableStateOf<Card?>(null) }
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
             val userRef = com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
             
             userRef.addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    if (snapshot != null && snapshot.exists()) {
                        user = snapshot.toObject(User::class.java)
                        userPfp = user?.pfp ?: ""
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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateCardDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "huzzwallet",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() },
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    if (userPfp.isNotEmpty()) {
                        AsyncImage(
                            model = userPfp,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Page Switcher
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SwitcherItem(
                            text = "Cards",
                            isSelected = selectedTab == "Cards",
                            onClick = { selectedTab = "Cards" }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SwitcherItem(
                            text = "History",
                            isSelected = selectedTab == "History",
                            onClick = { selectedTab = "History" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedTab == "Cards") {
                    CardContent(cards, user?.username ?: "User", onCardClick = { activeCard = it })
                } else {
                    HistoryContent()
                }
            }
        }
    }

    if (showCreateCardDialog && user != null) {
        CreateCardDialog(
            user = user!!,
            onDismiss = { showCreateCardDialog = false },
            onCreate = { newCard ->
                scope.launch {
                    authRepository.createCard(user!!.uid, newCard)
                    showCreateCardDialog = false
                }
            }
        )
    }

    if (activeCard != null && user != null) {
        CardTransmissionScreen(
            card = activeCard!!,
            username = user!!.username,
            onDismiss = { activeCard = null }
        )
    }
}
}

@Composable
fun SwitcherItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.background else Color.Transparent,
        shape = RoundedCornerShape(50),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun CardContent(cards: List<Card>, username: String, onCardClick: (Card) -> Unit) {
    if (cards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No cards yet. Tap + to create one!")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            cards.forEach { card ->
                CardItem(card, username, onClick = { onCardClick(card) })
            }
        }
    }
}

@Composable
fun CardItem(card: Card, username: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(card.backgroundColor),
        shadowElevation = 4.dp
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

@Composable
fun HistoryContent() {
    HistoryScreen()
}

@Composable
fun HistoryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "History Screen", style = MaterialTheme.typography.headlineMedium)
    }
}


