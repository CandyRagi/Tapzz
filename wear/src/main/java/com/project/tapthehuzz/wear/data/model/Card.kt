package com.project.tapthehuzz.wear.data.model

data class Card(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val link: String = "",
    val backgroundColor: Long = 0xFFFFFFFF, // Default white
    val imageUrl: String = "",
    val cardNumber: String = "",
    val category: String = "", // Default empty category
    val designId: String = "" // "design_one" or empty
)
