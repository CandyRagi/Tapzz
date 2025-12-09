package com.project.tapthehuzz.userInterface.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.font.Font
import com.project.tapthehuzz.R

val ValorantFont = FontFamily(
    Font(R.font.valorant_font)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = ValorantFont),
    displayMedium = TextStyle(fontFamily = ValorantFont),
    displaySmall = TextStyle(fontFamily = ValorantFont),
    headlineLarge = TextStyle(fontFamily = ValorantFont),
    headlineMedium = TextStyle(fontFamily = ValorantFont),
    headlineSmall = TextStyle(fontFamily = ValorantFont),
    titleLarge = TextStyle(fontFamily = ValorantFont),
    titleMedium = TextStyle(fontFamily = ValorantFont),
    titleSmall = TextStyle(fontFamily = ValorantFont),
    bodyLarge = TextStyle(
        fontFamily = ValorantFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = ValorantFont),
    bodySmall = TextStyle(fontFamily = ValorantFont),
    labelLarge = TextStyle(fontFamily = ValorantFont),
    labelMedium = TextStyle(fontFamily = ValorantFont),
    labelSmall = TextStyle(fontFamily = ValorantFont)
)
