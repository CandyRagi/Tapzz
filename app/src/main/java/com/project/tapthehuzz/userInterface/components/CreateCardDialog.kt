package com.project.tapthehuzz.userInterface.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
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
    card: Card? = null,
    onDismiss: () -> Unit,
    onSave: (Card) -> Unit
) {
    var cardName by remember { mutableStateOf(card?.name ?: "") }
    var cardLink by remember { mutableStateOf(card?.link ?: "") }
    var cardCategory by remember { mutableStateOf(if (card != null && card.category !in listOf("Social", "Game", "GitHub", "Business")) "Custom" else card?.category ?: "") }
    var customCategory by remember { mutableStateOf(if (card != null && card.category !in listOf("Social", "Game", "GitHub", "Business")) card.category else "") }
    var selectedDesign by remember { mutableStateOf(card?.designId ?: "") }
    var imageUrl by remember { mutableStateOf(card?.imageUrl ?: user.pfp) }

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
                    text = if (card == null) "Create Card" else "Edit Card",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Profile Picture Preview
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                         Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    }
                    
                    // Sync PFP Button
                    IconButton(
                        onClick = { imageUrl = user.pfp },
                        modifier = Modifier
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person, // Using Person icon as "Sync/Use Profile"
                            contentDescription = "Sync PFP",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                Text("Background Design", style = MaterialTheme.typography.labelLarge)
                
                val designs = listOf(
                    "design_one" to com.project.tapthehuzz.R.drawable.card_design_one,
                    "design_two" to com.project.tapthehuzz.R.drawable.card_design_two,
                    "design_three" to com.project.tapthehuzz.R.drawable.card_design_three
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Increased height to accommodate 2:1 aspect ratio cards
                        .padding(vertical = 8.dp)
                ) {
                    items(designs) { (designId, drawableId) ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(2f) // 2:1 Aspect Ratio
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedDesign == designId) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedDesign = designId }
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = drawableId),
                                contentDescription = designId,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
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
                            val cardId = card?.id ?: UUID.randomUUID().toString().substring(0, 8)
                            val cardNumber = card?.cardNumber ?: List(12) { (0..9).random() }.joinToString("")
                            val newCard = Card(
                                id = cardId,
                                userId = user.uid,
                                name = cardName,
                                link = cardLink,
                                backgroundColor = Color.White.toArgb().toLong(),
                                imageUrl = imageUrl,
                                cardNumber = cardNumber,
                                category = if (cardCategory == "Custom") customCategory.ifEmpty { "Custom" } else cardCategory.ifEmpty { "Uncategorized" },
                                designId = selectedDesign
                            )
                            onSave(newCard)
                        },
                        enabled = cardName.isNotEmpty() && cardLink.isNotEmpty()
                    ) {
                        Text(if (card == null) "Create" else "Save")
                    }
                }
            }
        }
    }
}
