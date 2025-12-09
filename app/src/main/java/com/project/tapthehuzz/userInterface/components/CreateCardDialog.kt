package com.project.tapthehuzz.userInterface.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.project.tapthehuzz.data.model.Card
import com.project.tapthehuzz.data.model.User
import java.util.UUID
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCardDialog(
    user: User,
    onDismiss: () -> Unit,
    onCreate: (Card) -> Unit
) {
    var cardName by remember { mutableStateOf("") }
    var cardLink by remember { mutableStateOf("") }
    var cardCategory by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.White) }
    var imageUrl by remember { mutableStateOf(user.pfp) } // Default to user PFP

    val colors = listOf(
        Color.White, Color(0xFFE1BEE7), Color(0xFFC5CAE9),
        Color(0xFFB2DFDB), Color(0xFFFFCCBC), Color(0xFFF0F4C3)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Card",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text("Card Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cardLink,
                    onValueChange = { cardLink = it },
                    label = { Text("Link") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Dropdown
                var expanded by remember { mutableStateOf(false) }
                val categories = listOf("Social", "Game", "GitHub", "Business", "Custom")
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = cardCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    cardCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (cardCategory == "Custom") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Enter Custom Category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Quick Link Options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (user.instagramLink.isNotEmpty()) {
                        SuggestionChip(
                            onClick = { cardLink = user.instagramLink },
                            label = { Text("Instagram") }
                        )
                    }
                    if (user.snapchatLink.isNotEmpty()) {
                        SuggestionChip(
                            onClick = { cardLink = user.snapchatLink },
                            label = { Text("Snapchat") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Background Color", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == color) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val cardId = UUID.randomUUID().toString().substring(0, 8)
                            val cardNumber = (10000000..99999999).random().toString()
                            val newCard = Card(
                                id = cardId,
                                userId = user.uid,
                                name = cardName,
                                link = cardLink,
                                backgroundColor = selectedColor.toArgb().toLong(),
                                imageUrl = imageUrl,
                                cardNumber = cardNumber,
                                category = if (cardCategory == "Custom") customCategory.ifEmpty { "Custom" } else cardCategory.ifEmpty { "Uncategorized" }
                            )
                            onCreate(newCard)
                        },
                        enabled = cardName.isNotEmpty() && cardLink.isNotEmpty()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}
