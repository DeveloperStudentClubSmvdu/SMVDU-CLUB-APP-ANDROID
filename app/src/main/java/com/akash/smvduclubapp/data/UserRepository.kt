package com.akash.smvduclubapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth,
                     private val firestore: FirebaseFirestore
){
    suspend fun signUp(email: String, password: String, username: String, reenterPassword: String):
            Result<User> {
        return try {
            if (password != reenterPassword) {
                return Result.Error(Exception("Passwords do not match"))
            }
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(username, email)
            //add user to firestore
            saveUserToFireStore(user)

            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun saveUserToFireStore(user: User) {
        firestore.collection("users").document(user.email).set(user).await()
    }

    suspend fun login(email: String, password: String): Result<User> =
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            // After successful login, fetch user details from Firestore or create a basic user
            val email = authResult.user?.email ?: ""
            val userData = try {
                val userDoc = firestore.collection("users").document(email).get().await()
                if (userDoc.exists()) {
                    val username = userDoc.getString("username") ?: "Unknown"
                    User(username, email)
                } else {
                    User("Unknown", email)
                }
            } catch (e: Exception) {
                User("Unknown", email)
            }

            Result.Success(userData)
        } catch (e: Exception) {
            Result.Error(e)
        }
}