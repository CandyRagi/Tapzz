package com.project.tapthehuzz.userInterface.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.tapthehuzz.R
import com.project.tapthehuzz.data.model.Card

val creditCardFont = FontFamily(
    Font(R.font.share_tech_mono)
)

@Composable
fun CardItem(
    card: Card, 
    username: String, 
    modifier: Modifier = Modifier.fillMaxWidth().height(220.dp)
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (card.designId.isNotEmpty()) Color.White else Color(card.backgroundColor),
        shadowElevation = 8.dp,
        tonalElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)) // Added outline
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Design
            val painter = when (card.designId) {
                "design_one" -> painterResource(id = R.drawable.card_design_one)
                "design_two" -> painterResource(id = R.drawable.card_design_two)
                "design_three" -> painterResource(id = R.drawable.card_design_three)
                "design_abstract" -> painterResource(id = R.drawable.card_design_abstract)
                "design_modern" -> painterResource(id = R.drawable.card_design_modern)
                "design_premium" -> painterResource(id = R.drawable.card_design_premium)
                else -> null
            }

            if (painter != null) {
                Image(
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
                        Brush.verticalGradient(
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
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                blurRadius = 2f
                            )
                        )
                    )
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            shadow = Shadow(
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
                        fontFamily = creditCardFont, // Custom font
                        fontWeight = FontWeight.Normal, // Regular weight for this font usually looks bold enough, or keep bold if needed
                        fontSize = 22.sp, // Smaller font size
                        color = Color.White,
                        shadow = Shadow(
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
                                shadow = Shadow(
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
