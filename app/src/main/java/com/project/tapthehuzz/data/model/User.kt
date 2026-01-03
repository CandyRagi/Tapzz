package com.project.tapthehuzz.data.model


// The is the user model Class (Similar to Card Model Devs can add or remove details)
data class User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val pfp: String = "",
    val instagramLink: String = "",
    val snapchatLink: String = "",
    val tiktokLink: String = "",
    val youtubeLink: String = "",
    val facebookLink: String = "",
    val valorantLink: String = "",
    val discordLink: String = "",
    val whatsappLink: String = "",
    val bruzzList: List<String> = emptyList(),
    val huzzList: List<String> = emptyList(),
    val quickAccessList: List<String> = emptyList(),
    val tapHistory: List<TapHistoryItem> = emptyList()
)


// I haven't Implemeted History Feature yet But it will be coming soon
data class TapHistoryItem(
    val location: String = "",
    val time: Long = 0,
    val linkType: String = ""
)
