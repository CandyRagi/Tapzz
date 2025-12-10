package com.project.tapthehuzz.userInterface.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.tapthehuzz.data.model.User
import com.project.tapthehuzz.data.repository.AuthRepository
import com.project.tapthehuzz.userInterface.components.EditProfileDialog
import com.project.tapthehuzz.userInterface.components.SocialLinkDialog
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    var user by remember { mutableStateOf<User?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showSocialDialog by remember { mutableStateOf(false) }
    var showUnlinkDialog by remember { mutableStateOf(false) }
    var selectedPlatform by remember { mutableStateOf("") }
    var currentSocialLink by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    LaunchedEffect(Unit) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid).get().addOnSuccessListener { document ->
                    user = document.toObject(User::class.java)
                }
        }
    }

    if (showEditDialog && user != null) {
        EditProfileDialog(
            user = user!!,
            onDismiss = { showEditDialog = false },
            onProfileUpdated = {
                // Refresh user data
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users")
                        .document(currentUser.uid).get().addOnSuccessListener { document ->
                            user = document.toObject(User::class.java)
                        }
                }
            }
        )
    }

    if (showSocialDialog) {
        SocialLinkDialog(
            platformName = selectedPlatform,
            currentLink = currentSocialLink,
            onDismiss = { showSocialDialog = false },
            onSave = { newLink: String ->
                if (user != null) {
                    val fieldToUpdate = when (selectedPlatform) {
                        "Instagram" -> "instagramLink"
                        "Snapchat" -> "snapchatLink"
                        "Facebook" -> "facebookLink"
                        "Valorant" -> "valorantLink"
                        "Discord" -> "discordLink"
                        "WhatsApp" -> "whatsappLink"
                        else -> ""
                    }
                    if (fieldToUpdate.isNotEmpty()) {
                        val updates: Map<String, Any> = mapOf(fieldToUpdate to newLink)
                        scope.launch {
                            val result = authRepository.updateUserProfile(user!!.uid, updates)
                            if (result.isSuccess) {
                                 com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users")
                                    .document(user!!.uid).get().addOnSuccessListener { document ->
                                        user = document.toObject(User::class.java)
                                    }
                            }
                        }
                    }
                }
                showSocialDialog = false
            }
        )
    }

    if (showUnlinkDialog) {
        AlertDialog(
            onDismissRequest = { showUnlinkDialog = false },
            title = { Text("Unlink Account") },
            text = { Text("Are you sure you want to unlink $selectedPlatform?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (user != null) {
                            val fieldToUpdate = when (selectedPlatform) {
                                "Instagram" -> "instagramLink"
                                "Snapchat" -> "snapchatLink"
                                "Facebook" -> "facebookLink"
                                "Valorant" -> "valorantLink"
                                "Discord" -> "discordLink"
                                "WhatsApp" -> "whatsappLink"
                                else -> ""
                            }
                            if (fieldToUpdate.isNotEmpty()) {
                                val updates: Map<String, Any> = mapOf(fieldToUpdate to "")
                                scope.launch {
                                    val result = authRepository.updateUserProfile(user!!.uid, updates)
                                    if (result.isSuccess) {
                                         com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users")
                                            .document(user!!.uid).get().addOnSuccessListener { document ->
                                                user = document.toObject(User::class.java)
                                            }
                                    }
                                }
                            }
                        }
                        showUnlinkDialog = false
                    }
                ) {
                    Text("Unlink", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlinkDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .clickable { showEditDialog = true }
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Profile Picture
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        if (user != null && user!!.pfp.isNotEmpty()) {
                            AsyncImage(
                                model = user!!.pfp,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.padding(24.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = user?.username ?: "Loading...",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Accounts Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "LINKED Accounts",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Instagram
                    SocialAccountItem(
                        name = "Instagram",
                        iconUrl = "https://img.icons8.com/color/96/instagram-new.png",
                        link = user?.instagramLink ?: "",
                        onLinkClick = {
                            if (user?.instagramLink.isNullOrEmpty()) {
                                selectedPlatform = "Instagram"
                                currentSocialLink = ""
                                showSocialDialog = true
                            } else {
                                selectedPlatform = "Instagram"
                                showUnlinkDialog = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Snapchat
                    SocialAccountItem(
                        name = "Snapchat",
                        iconUrl = "https://img.icons8.com/color/96/snapchat-circled-logo--v1.png",
                        link = user?.snapchatLink ?: "",
                        onLinkClick = {
                            if (user?.snapchatLink.isNullOrEmpty()) {
                                selectedPlatform = "Snapchat"
                                currentSocialLink = ""
                                showSocialDialog = true
                            } else {
                                selectedPlatform = "Snapchat"
                                showUnlinkDialog = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Facebook
                    SocialAccountItem(
                        name = "Facebook",
                        iconUrl = "https://img.icons8.com/color/96/facebook-new.png",
                        link = user?.facebookLink ?: "",
                        onLinkClick = {
                            if (user?.facebookLink.isNullOrEmpty()) {
                                selectedPlatform = "Facebook"
                                currentSocialLink = ""
                                showSocialDialog = true
                            } else {
                                selectedPlatform = "Facebook"
                                showUnlinkDialog = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Valorant
                    SocialAccountItem(
                        name = "Valorant",
                        iconUrl = "https://img.icons8.com/color/96/valorant.png",
                        link = user?.valorantLink ?: "",
                        onLinkClick = {
                            if (user?.valorantLink.isNullOrEmpty()) {
                                selectedPlatform = "Valorant"
                                currentSocialLink = ""
                                showSocialDialog = true
                            } else {
                                selectedPlatform = "Valorant"
                                showUnlinkDialog = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Discord
                    SocialAccountItem(
                        name = "Discord",
                        iconUrl = "https://img.icons8.com/color/96/discord-logo.png",
                        link = user?.discordLink ?: "",
                        onLinkClick = {
                            if (user?.discordLink.isNullOrEmpty()) {
                                selectedPlatform = "Discord"
                                currentSocialLink = ""
                                showSocialDialog = true
                            } else {
                                selectedPlatform = "Discord"
                                showUnlinkDialog = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // WhatsApp
                    SocialAccountItem(
                        name = "WhatsApp",
                        iconUrl = "https://img.icons8.com/color/96/whatsapp--v1.png",
                        link = user?.whatsappLink ?: "",
                        onLinkClick = {
                            if (user?.whatsappLink.isNullOrEmpty()) {
                                selectedPlatform = "WhatsApp"
                                currentSocialLink = ""
                                showSocialDialog = true
                            } else {
                                selectedPlatform = "WhatsApp"
                                showUnlinkDialog = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SocialAccountItem(
    name: String,
    iconUrl: String,
    link: String,
    onLinkClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLinkClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(40.dp),
                shadowElevation = 2.dp
            ) {
                AsyncImage(
                    model = iconUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        if (link.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Linked",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        } else {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Link Account",
                    modifier = Modifier.padding(6.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
