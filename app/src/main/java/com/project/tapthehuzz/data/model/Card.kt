package com.project.tapthehuzz.data.model


// This is the card model (Dev's can add or remove card details From here)
// The Card is intialized with these default values
data class Card(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val link: String = "",
    val backgroundColor: Long = 0xFFFFFFFF,
    val imageUrl: String = "",
    val cardNumber: String = "",
    val category: String = "",
    val designId: String = ""
)
