package com.project.tapthehuzz.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.tapthehuzz.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

    suspend fun uploadImageToCloudinary(imageUri: android.net.Uri): String? {
        return try {
            val cloudName = "dczuk4cxa"
            val uploadPreset = "StduySage"
            val url = java.net.URL("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            val boundary = "Boundary-" + System.currentTimeMillis()
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

            val outputStream = connection.outputStream
            val writer = java.io.PrintWriter(java.io.OutputStreamWriter(outputStream, "UTF-8"), true)

            // Add upload_preset
            writer.append("--$boundary").append("\r\n")
            writer.append("Content-Disposition: form-data; name=\"upload_preset\"").append("\r\n")
            writer.append("\r\n").append(uploadPreset).append("\r\n")

            // Add file
            writer.append("--$boundary").append("\r\n")
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"").append("\r\n")
            writer.append("Content-Type: image/jpeg").append("\r\n")
            writer.append("\r\n")
            writer.flush()

            val inputStream = java.net.URL(imageUri.toString()).openStream()
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            inputStream.close()

            writer.append("\r\n").flush()
            writer.append("--$boundary--").append("\r\n")
            writer.close()

            val responseCode = connection.responseCode
            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val responseStream = java.io.BufferedReader(java.io.InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (responseStream.readLine().also { line = it } != null) {
                    response.append(line)
                }
                responseStream.close()
                val jsonResponse = org.json.JSONObject(response.toString())
                jsonResponse.getString("secure_url")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createCard(userId: String, card: com.project.tapthehuzz.data.model.Card): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).collection("cards")
                .document(card.id).set(card).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
