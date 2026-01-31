package com.project.tapthehuzz.userInterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.tapthehuzz.data.model.Card
import com.project.tapthehuzz.data.model.User
import com.project.tapthehuzz.userInterface.components.CardItem
import com.project.tapthehuzz.userInterface.components.EmptyStateCard
import com.project.tapthehuzz.userInterface.theme.GlassBorder
import com.project.tapthehuzz.userInterface.theme.GlassSurface
import com.project.tapthehuzz.userInterface.theme.GlassSurfaceLight
import com.project.tapthehuzz.userInterface.theme.AccentRed
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AllCardsScreen(
    cards: List<Card>,
    user: User,
    onCardClick: (Card) -> Unit,
    onAddClick: () -> Unit,
    onEditCard: (Card) -> Unit,
    onDeleteCard: (Card) -> Unit,
    onToggleQuickAccess: (Card) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var cardForMenu by remember { mutableStateOf<Card?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Filter and sort cards
    val filteredCards = remember(cards, searchQuery, selectedCategoryFilter) {
        cards.filter { card ->
            val matchesSearch = card.name.contains(searchQuery, ignoreCase = true)
            val matchesCategory = if (selectedCategoryFilter == "All") true else {
                val category = card.category.ifEmpty { "Uncategorized" }
                if (selectedCategoryFilter == "Custom") {
                    category !in listOf("Social", "Game", "GitHub", "Business", "Uncategorized")
                } else {
                    category == selectedCategoryFilter
                }
            }
            matchesSearch && matchesCategory
        }.reversed()
    }

    Column(modifier = Modifier.fillMaxSize()) {


        // Search Bar and Add Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Glassmorphic Search Bar
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .border(
                        width = 1.dp,
                        color = GlassBorder,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = GlassSurface.copy(alpha = 0.6f)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxSize(),
                    placeholder = {
                        Text(
                            "Search cards...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Glassmorphic Filter Button
            Box {
                Surface(
                    shape = CircleShape,
                    color = GlassSurfaceLight.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(52.dp)
                        .border(
                            width = 1.dp,
                            color = GlassBorder,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { showFilterMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Filter Cards",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false },
                    modifier = Modifier
                        .background(GlassSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                ) {
                    val filterOptions = listOf("All", "Social", "Game", "GitHub", "Business", "Custom", "Uncategorized")
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedCategoryFilter = option
                                showFilterMenu = false
                            },
                            leadingIcon = if (selectedCategoryFilter == option) {
                                { Icon(Icons.Filled.Check, contentDescription = "Selected", tint = AccentRed) }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Glassmorphic Add Button with Accent
            Surface(
                shape = CircleShape,
                color = AccentRed,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .clickable { onAddClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Card",
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredCards.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "No cards found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    EmptyStateCard(
                        onClick = onAddClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(bottom = 50.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                items(filteredCards) { card ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 1.02f else 1f)

                    Box {
                        CardItem(
                            card = card,
                            username = user.username,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .combinedClickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onCardClick(card) },
                                    onLongClick = { cardForMenu = card }
                                )
                        )
                    }
                }
            }
        }
    }

    // Glassmorphic Bottom Sheet
    if (cardForMenu != null) {
        ModalBottomSheet(
            onDismissRequest = { cardForMenu = null },
            sheetState = sheetState,
            containerColor = GlassSurface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(GlassBorder, Color.Transparent)
                        ),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GlassBorder)
                )
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = cardForMenu!!.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    val category = cardForMenu!!.category.ifEmpty { "Uncategorized" }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GlassSurfaceLight.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                HorizontalDivider(color = GlassBorder, modifier = Modifier.padding(bottom = 16.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Edit Button
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = GlassSurfaceLight.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onEditCard(cardForMenu!!)
                                scope.launch { sheetState.hide() }.invokeOnCompletion { 
                                    if (!sheetState.isVisible) cardForMenu = null 
                                }
                            }
                    ) {
                        ListItem(
                            headlineContent = { Text("Edit Details", fontWeight = FontWeight.SemiBold) },
                            leadingContent = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }

                    // Quick Access Button
                    val isInQuickAccess = cardForMenu!!.id in user.quickAccessList
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = GlassSurfaceLight.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                         modifier = Modifier.clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onToggleQuickAccess(cardForMenu!!)
                                scope.launch { sheetState.hide() }.invokeOnCompletion { 
                                    if (!sheetState.isVisible) cardForMenu = null 
                                }
                            }
                    ) {
                        ListItem(
                            headlineContent = { Text(if (isInQuickAccess) "Remove from Quick Access" else "Add to Quick Access", fontWeight = FontWeight.SemiBold) },
                            leadingContent = { 
                                Icon(
                                    if (isInQuickAccess) Icons.Filled.Star else Icons.Filled.StarBorder, 
                                    contentDescription = null,
                                    tint = if (isInQuickAccess) AccentRed else MaterialTheme.colorScheme.onSurface
                                ) 
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }

                    // Delete Button
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AccentRed.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.3f)),
                         modifier = Modifier.clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onDeleteCard(cardForMenu!!)
                                scope.launch { sheetState.hide() }.invokeOnCompletion { 
                                    if (!sheetState.isVisible) cardForMenu = null 
                                }
                            }
                    ) {
                        ListItem(
                            headlineContent = { Text("Delete", color = AccentRed, fontWeight = FontWeight.SemiBold) },
                            leadingContent = { Icon(Icons.Filled.Delete, contentDescription = null, tint = AccentRed) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}
