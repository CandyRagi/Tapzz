package com.project.tapthehuzz.userInterface.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import com.project.tapthehuzz.userInterface.theme.GlassBorder
import com.project.tapthehuzz.userInterface.theme.GlassSurface
import com.project.tapthehuzz.userInterface.theme.AccentRed

@Composable
fun EmptyStateCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    showIcon: Boolean = true
) {
    // Glassmorphic Empty State Card
    Surface(
        modifier = modifier
            .border(
                width = 2.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = GlassSurface.copy(alpha = 0.4f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showIcon) {
                    // Glassmorphic Add Button
                    Surface(
                        shape = CircleShape,
                        color = AccentRed,
                        modifier = Modifier
                            .size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create Card",
                            tint = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                
                if (text != null) {
                    if (showIcon) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
