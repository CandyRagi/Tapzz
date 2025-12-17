package com.project.tapthehuzz.wear.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.tapthehuzz.wear.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, username: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User creation failed")
            
            val newUser = User(
                uid = uid,
                email = email,
                username = username
            )
            
            saveUser(newUser).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveUser(user: User) = firestore.collection("users").document(user.uid).set(user)

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
    }
    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("No user logged in")
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCard(userId: String, card: com.project.tapthehuzz.wear.data.model.Card): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).collection("cards")
                .document(card.id).set(card).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCard(userId: String, cardId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).collection("cards")
                .document(cardId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleQuickAccess(userId: String, cardId: String, addToQuickAccess: Boolean): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            if (addToQuickAccess) {
                userRef.update("quickAccessList", com.google.firebase.firestore.FieldValue.arrayUnion(cardId)).await()
            } else {
                userRef.update("quickAccessList", com.google.firebase.firestore.FieldValue.arrayRemove(cardId)).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateQuickAccessListOrder(userId: String, newOrder: List<String>): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("quickAccessList", newOrder).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
