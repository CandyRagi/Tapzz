package com.project.tapthehuzz.wear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun TapTheHuzzWearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}
