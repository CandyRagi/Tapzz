package com.project.tapthehuzz.userInterface.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.tapthehuzz.data.model.Card
import com.project.tapthehuzz.userInterface.components.EmptyStateCard

@Composable
fun AllCardsScreen(
    cards: List<Card>,
    username: String,
    onCardClick: (Card) -> Unit,
    onAddClick: () -> Unit
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
                .padding(bottom = 16.dp),
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
                            .height(200.dp)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 40.dp) // Move up visually
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredCards) { card ->
                    CardItem(
                        card = card,
                        username = username,
                        onClick = { onCardClick(card) }
                    )
                }
            }
        }
    }
}
