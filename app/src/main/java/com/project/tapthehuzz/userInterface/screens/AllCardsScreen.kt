package com.project.tapthehuzz.userInterface.screens

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.tapthehuzz.data.model.Card
import com.project.tapthehuzz.data.model.User
import com.project.tapthehuzz.userInterface.components.CardItem
import com.project.tapthehuzz.userInterface.components.EmptyStateCard

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
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
            modifier = Modifier
                .fillMaxWidth()
                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxSize(),
                    placeholder = {
                        Text(
                            "Search cards...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
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

            // Filter Button
            Box {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { showFilterMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Filled.List, // Using List icon as filter/sort
                        contentDescription = "Filter Cards",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
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
                                { Icon(Icons.Filled.Check, contentDescription = "Selected") }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Add Button
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(50.dp)
                    .clickable { onAddClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Card",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                items(filteredCards) { card ->
                    var showMenu by remember { mutableStateOf(false) }
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 1.05f else 1f)

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
                                    onLongClick = { showMenu = true }
                                )
                        )
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Details") },
                                onClick = {
                                    onEditCard(card)
                                    showMenu = false
                                },
                                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) }
                            )
                            val isInQuickAccess = card.id in user.quickAccessList
                            DropdownMenuItem(
                                text = { Text(if (isInQuickAccess) "Remove from Quick Access" else "Add to Quick Access") },
                                onClick = {
                                    onToggleQuickAccess(card)
                                    showMenu = false
                                },
                                leadingIcon = { Icon(if (isInQuickAccess) Icons.Filled.Star else Icons.Filled.StarBorder, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    onDeleteCard(card)
                                    showMenu = false
                                },
                                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    }
                }
            }
        }
    }
}
