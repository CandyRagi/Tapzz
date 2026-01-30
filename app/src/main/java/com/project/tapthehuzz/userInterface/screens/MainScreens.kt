package com.project.tapthehuzz.userInterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.layout.aspectRatio
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import com.project.tapthehuzz.R

import com.project.tapthehuzz.userInterface.components.CardItem

// Removed creditCardFont as it is now in CardItem.kt

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
    val context = androidx.compose.ui.platform.LocalContext.current

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
                            text = androidx.compose.ui.text.buildAnnotatedString {
                                append(" ")
                                withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.Red)) {
                                    append("RIZZ")
                                }
                                append(" Wallet")
                            },
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
                    if (user != null) {
                        androidx.compose.animation.AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                if (targetState == "All Cards") {
                                    androidx.compose.animation.slideInHorizontally { width -> width } + androidx.compose.animation.fadeIn() togetherWith
                                            androidx.compose.animation.slideOutHorizontally { width -> -width } + androidx.compose.animation.fadeOut()
                                } else {
                                    androidx.compose.animation.slideInHorizontally { width -> -width } + androidx.compose.animation.fadeIn() togetherWith
                                            androidx.compose.animation.slideOutHorizontally { width -> width } + androidx.compose.animation.fadeOut()
                                }
                            },
                            label = "Tab Content Animation"
                        ) { targetTab ->
                            val filteredCards = if (targetTab == "Quick Access") {
                                cards.filter { it.id in user!!.quickAccessList }
                            } else {
                                cards
                            }

                            CardContent(
                                cards = filteredCards,
                                allCards = cards,
                                user = user!!,
                                onCardClick = { activeCard = it },
                                onAddClick = {
                                    editingCard = null
                                    showCreateCardDialog = true
                                },
                                onEditCard = { card ->
                                    editingCard = card
                                    showCreateCardDialog = true
                                },
                                onDeleteCard = { card ->
                                    scope.launch {
                                        authRepository.deleteCard(user!!.uid, card.id)
                                    }
                                },
                                onToggleQuickAccess = { card ->
                                    scope.launch {
                                        val isInQuickAccess = card.id in user!!.quickAccessList
                                        if (!isInQuickAccess && user!!.quickAccessList.size >= 9) {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Quick Access section full. Remove cards to add others.",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            authRepository.toggleQuickAccess(user!!.uid, card.id, !isInQuickAccess)
                                        }
                                    }
                                },
                                onReorderCards = { newOrder ->
                                    scope.launch {
                                        authRepository.updateQuickAccessListOrder(user!!.uid, newOrder)
                                    }
                                },
                                viewMode = targetTab
                            )
                        }
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
                        if (selectedTab == "Quick Access") {
                            authRepository.toggleQuickAccess(user!!.uid, newCard.id, true)
                        }
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

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CardContent(
    cards: List<Card>,
    allCards: List<Card>,
    user: User,
    onCardClick: (Card) -> Unit,
    onAddClick: () -> Unit,
    onEditCard: (Card) -> Unit,
    onDeleteCard: (Card) -> Unit,
    onToggleQuickAccess: (Card) -> Unit,
    onReorderCards: (List<String>) -> Unit,
    viewMode: String
) {
    var isEditOrderMode by remember { mutableStateOf(false) }

    LaunchedEffect(viewMode) {
        isEditOrderMode = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (viewMode == "Quick Access") {
            // Quick Access View (Horizontal Pager or List)
            if (cards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateCard(
                        onClick = onAddClick,
                        text = "Tap to create a new card\n\n- OR -\n\nLong-press a card in 'All Cards'\nto add it here",
                        showIcon = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(bottom = 50.dp)
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

                            BoxWithConstraints(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                val screenWidth = maxWidth
                                val cardWidth = screenWidth // Full width
                                val horizontalPadding = 0.dp

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {


                                    LazyRow(
                                        state = listState,
                                        flingBehavior = flingBehavior,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(horizontal = horizontalPadding)
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

    

                                            Box {
                                                CardItem(
                                                    card = card,
                                                    username = user.username,
                                                    modifier = Modifier
                                                        .width(cardWidth)
                                                        .height(220.dp)
                                                        .graphicsLayer {
                                                            scaleX = scale
                                                            scaleY = scale
                                                            alpha = scale
                                                        }
                                                        .clickable(
                                                            onClick = {
                                                                onCardClick(card)
                                                            }
                                                        )
                                                )
                                            }
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
                                            colors = ButtonDefaults.filledTonalButtonColors(
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
                            cards = cards, // This will be all cards if viewMode is "All Cards"
                            user = user,
                            onCardClick = onCardClick,
                            onAddClick = onAddClick,
                            onEditCard = onEditCard,
                            onDeleteCard = onDeleteCard,
                            onToggleQuickAccess = onToggleQuickAccess
                        )
                    }
                }
            }
        } else {
            // All Cards View
            AllCardsScreen(
                cards = cards, // This will be all cards if viewMode is "All Cards"
                user = user,
                onCardClick = onCardClick,
                onAddClick = onAddClick,
                onEditCard = onEditCard,
                onDeleteCard = onDeleteCard,
                onToggleQuickAccess = onToggleQuickAccess
            )
        }

        if (isEditOrderMode) {
            ReorderOverlay(
                cards = cards,
                onSave = { newOrder ->
                    onReorderCards(newOrder)
                    isEditOrderMode = false
                },
                onCancel = { isEditOrderMode = false }
            )
        }
    }
}


@Composable
fun HistoryContent() {
    HistoryScreen()
}



@Composable
fun ReorderOverlay(
    cards: List<Card>,
    onSave: (List<String>) -> Unit,
    onCancel: () -> Unit
) {
    var currentOrder by remember { mutableStateOf(cards) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(enabled = true) {}, // Block clicks
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tap two cards to swap",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentOrder.size, key = { index -> currentOrder[index].id }) { index ->
                    val card = currentOrder[index]
                    val isSelected = selectedIndex == index

                    Box(
                        modifier = Modifier
                            .aspectRatio(1.6f) // Card aspect ratio
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                if (selectedIndex == null) {
                                    selectedIndex = index
                                } else {
                                    if (selectedIndex != index) {
                                        // Swap
                                        val newList = currentOrder.toMutableList()
                                        java.util.Collections.swap(newList, selectedIndex!!, index)
                                        currentOrder = newList
                                        selectedIndex = null
                                    } else {
                                        // Deselect
                                        selectedIndex = null
                                    }
                                }
                            }
                    ) {
                        // Mini Card Representation
                        Surface(
                            color = if (card.designId.isNotEmpty()) Color.White else Color(card.backgroundColor),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (card.imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = card.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = card.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        // Overlay to dim if not selected (optional, but good for focus)
                        if (selectedIndex != null && selectedIndex != index) {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Cancel")
                }

                androidx.compose.material3.Button(
                    onClick = { onSave(currentOrder.map { it.id }) }
                ) {
                    Text("Save Order")
                }
            }
        }
    }
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