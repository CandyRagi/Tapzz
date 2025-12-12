package com.project.tapthehuzz.userInterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.project.tapthehuzz.data.model.Card
import com.project.tapthehuzz.userInterface.components.CreateCardDialog
import com.project.tapthehuzz.userInterface.components.EmptyStateCard
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.project.tapthehuzz.data.model.User
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
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
    var selectedTab by remember { mutableStateOf("Quick Access") }
    var userPfp by remember { mutableStateOf("") }
    var user by remember { mutableStateOf<User?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    var showCreateCardDialog by remember { mutableStateOf(false) }
    var editingCard by remember { mutableStateOf<Card?>(null) }
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
            bottomBar = {
                // Page Switcher (Bottom)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent, // No background
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly // Left center and Right center
                    ) {
                        SwitcherItem(
                            text = "Quick Access",
                            isSelected = selectedTab == "Quick Access",
                            onClick = { selectedTab = "Quick Access" }
                        )
                        SwitcherItem(
                            text = "All Cards",
                            isSelected = selectedTab == "All Cards",
                            onClick = { selectedTab = "All Cards" }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .padding(start = 19.dp, top = 12.dp, end = 16.dp)
            ) {
                // Header
            if (selectedTab != "All Cards") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " RIZZ Wallet",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (0).sp,
                            fontSize = 23.sp
                        )
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Profile Picture
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { onProfileClick() },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
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
                                    modifier = Modifier.padding(10.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab != "All Cards") {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                CardContent(
                    cards = cards,
                    username = user?.username ?: "User",
                    onCardClick = { activeCard = it },
                    onAddClick = { 
                        editingCard = null
                        showCreateCardDialog = true 
                    },
                    onEditCard = { card ->
                        editingCard = card
                        showCreateCardDialog = true
                    },
                    onSyncPfp = { card ->
                        if (user != null) {
                            val updatedCard = card.copy(imageUrl = user!!.pfp)
                            scope.launch {
                                authRepository.createCard(user!!.uid, updatedCard)
                            }
                        }
                    },
                    viewMode = selectedTab
                )
            }
        }
    }
    }

    if (showCreateCardDialog && user != null) {
        CreateCardDialog(
            user = user!!,
            card = editingCard,
            onDismiss = { 
                showCreateCardDialog = false 
                editingCard = null
            },
            onSave = { newCard ->
                scope.launch {
                    authRepository.createCard(user!!.uid, newCard)
                    showCreateCardDialog = false
                    editingCard = null
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

@Composable
fun SwitcherItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(24.dp) // Underline width
                    .height(2.dp) // Underline height
                    .clip(RoundedCornerShape(1.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun CardContent(
    cards: List<Card>, 
    username: String, 
    onCardClick: (Card) -> Unit,
    onAddClick: () -> Unit,
    onEditCard: (Card) -> Unit,
    onSyncPfp: (Card) -> Unit,
    viewMode: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (cards.isEmpty() && viewMode == "Quick Access") {
             Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    onClick = onAddClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 40.dp) // Move up visually
                )
            }
        } else {
            androidx.compose.animation.AnimatedContent(
                targetState = viewMode,
                transitionSpec = {
                    if (targetState == "All Cards") {
                        androidx.compose.animation.slideInHorizontally { width -> width } + androidx.compose.animation.fadeIn() togetherWith
                        androidx.compose.animation.slideOutHorizontally { width -> -width } + androidx.compose.animation.fadeOut()
                    } else {
                        androidx.compose.animation.slideInHorizontally { width -> -width } + androidx.compose.animation.fadeIn() togetherWith
                        androidx.compose.animation.slideOutHorizontally { width -> width } + androidx.compose.animation.fadeOut()
                    }
                },
                label = "Tab Animation"
            ) { targetViewMode ->
                if (targetViewMode == "Quick Access") {
                    // Horizontal List
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                        val flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(lazyListState = listState)
                        var currentCenteredIndex by remember { mutableIntStateOf(0) }

                        LaunchedEffect(listState) {
                            snapshotFlow { listState.layoutInfo }
                                .collect { layoutInfo ->
                                    val viewportCenter = layoutInfo.viewportEndOffset / 2f
                                    val centeredItem = layoutInfo.visibleItemsInfo.minByOrNull { item ->
                                        kotlin.math.abs((item.offset + item.size / 2f) - viewportCenter)
                                    }
                                    if (centeredItem != null) {
                                        currentCenteredIndex = centeredItem.index
                                    }
                                }
                        }
                        
                        androidx.compose.foundation.layout.BoxWithConstraints(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            val screenWidth = maxWidth
                            val cardWidth = screenWidth // Full width
                            val horizontalPadding = 0.dp
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                androidx.compose.foundation.lazy.LazyRow(
                                    state = listState,
                                    flingBehavior = flingBehavior,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = horizontalPadding)
                                ) {
                                items(cards.size) { index ->
                                    val card = cards[index]
                                    val scale by remember(index) {
                                        derivedStateOf {
                                            val currentItem = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                                            if (currentItem != null) {
                                                val viewportCenter = listState.layoutInfo.viewportEndOffset / 2f
                                                val itemCenter = currentItem.offset + currentItem.size / 2f
                                                val distance = kotlin.math.abs(viewportCenter - itemCenter)
                                                val maxDistance = viewportCenter 
                                                val scale = 1f - (distance / maxDistance) * 0.2f 
                                                scale.coerceIn(0.8f, 1f)
                                            } else {
                                                0.8f 
                                            }
                                        }
                                    }

                                    CardItem(
                                        card = card, 
                                        username = username, 
                                        modifier = Modifier
                                            .width(cardWidth)
                                            .height(220.dp)
                                            .graphicsLayer {
                                                scaleX = scale
                                                scaleY = scale
                                                alpha = scale
                                            },
                                        onClick = { onCardClick(card) }
                                    )
                                }
                            }

                            if (cards.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(120.dp))
                                
                                androidx.compose.material3.FilledTonalButton(
                                    onClick = { 
                                        if (currentCenteredIndex in cards.indices) {
                                            onCardClick(cards[currentCenteredIndex]) 
                                        }
                                    },
                                    modifier = Modifier
                                        .height(50.dp),
                                    shape = CircleShape,
                                    colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color.Gray,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = "Tap",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                    // All Cards (Vertical List)
                    AllCardsScreen(
                        cards = cards,
                        username = username,
                        onCardClick = onCardClick,
                        onAddClick = onAddClick,
                        onEditCard = onEditCard,
                        onSyncPfp = onSyncPfp
                    )
                }
            }
        }
    }
}

@Composable
fun CardItem(
    card: Card, 
    username: String, 
    modifier: Modifier = Modifier.fillMaxWidth().height(220.dp),
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (card.designId.isNotEmpty()) Color.White else Color(card.backgroundColor),
        shadowElevation = 8.dp,
        tonalElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)) // Added outline
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Design
            val painter = when (card.designId) {
                "design_one" -> androidx.compose.ui.res.painterResource(id = com.project.tapthehuzz.R.drawable.card_design_one)
                "design_two" -> androidx.compose.ui.res.painterResource(id = com.project.tapthehuzz.R.drawable.card_design_two)
                "design_three" -> androidx.compose.ui.res.painterResource(id = com.project.tapthehuzz.R.drawable.card_design_three)
                "design_abstract" -> androidx.compose.ui.res.painterResource(id = com.project.tapthehuzz.R.drawable.card_design_abstract)
                "design_modern" -> androidx.compose.ui.res.painterResource(id = com.project.tapthehuzz.R.drawable.card_design_modern)
                "design_premium" -> androidx.compose.ui.res.painterResource(id = com.project.tapthehuzz.R.drawable.card_design_premium)
                else -> null
            }

            if (painter != null) {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Overlay for readability (optional, subtle gradient)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.4f))
                        )
                    )
            )

            // Card Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Heading and Card Name
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Tap The Huzz",
                        style = MaterialTheme.typography.headlineSmall.copy( // Adjusted size
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.White,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                blurRadius = 2f
                            )
                        )
                    )
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                blurRadius = 2f
                            )
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Card Number
                Text(
                    text = card.cardNumber.chunked(4).joinToString(" "), // Decreased space between groups
                    style = MaterialTheme.typography.headlineMedium.copy( 
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp, // Smaller font size
                        color = Color.White,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            blurRadius = 4f
                        ),
                        letterSpacing = 2.sp // Decreased letter spacing
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Row: Name and Expiry (Simulated)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CARD HOLDER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = username.uppercase(), // Username below CARD HOLDER
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    }

                    // Profile Picture (Small, like a hologram or ID)
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        if (card.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = card.imageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
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


