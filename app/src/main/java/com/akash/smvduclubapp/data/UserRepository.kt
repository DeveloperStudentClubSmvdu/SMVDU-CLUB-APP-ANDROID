package com.akash.smvduclubapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth,
                     private val firestore: FirebaseFirestore
){
    suspend fun signUp(email: String, password: String, username: String,reenterPassword: String):
            Result<Boolean> {
        return try {
            if (password != reenterPassword) {
                return Result.Error(Exception("Passwords do not match"))
            }
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(username,email)
            //add user to firestore
            saveUserToFireStore(user)

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
            }

    private suspend fun saveUserToFireStore(user: User) {
        firestore.collection("users").document(user.email).set(user).await()
    }
    suspend fun login(email: String, password: String): Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
}