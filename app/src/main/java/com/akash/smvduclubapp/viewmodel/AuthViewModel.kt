@file:OptIn(SupabaseInternal::class, SupabaseExperimental::class)
package com.akash.smvduclubapp.viewmodel

import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.smvduclubapp.Injection
import com.akash.smvduclubapp.data.Result
import com.akash.smvduclubapp.data.Result.Success
import com.akash.smvduclubapp.data.User
import com.akash.smvduclubapp.data.UserRepository
import com.akash.smvduclubapp.database.saveUserToSupabase
import com.akash.smvduclubapp.database.supabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import io.github.jan.supabase.gotrue.providers.builtin.Email


class AuthViewModel : ViewModel() {
    private val userRepository: UserRepository
    init {
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<Result<User>>()
    val authResult: LiveData<Result<User>> get() = _authResult

    private val _googleSignInResult = MutableLiveData<FirebaseUser?>()
    val googleSignInResult: LiveData<FirebaseUser?> get() = _googleSignInResult

    // Add a loading state if needed
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Current user data
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    // Add a new LiveData for verification state
    private val _verificationStatus = MutableLiveData<Boolean>()
    val verificationStatus: LiveData<Boolean> = _verificationStatus


    // Check if the user is authenticated
    fun isUserAuthenticated(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }


    fun signUp(email: String, password: String, username: String, reenterPassword: String) {
        _isLoading.value = true
        val auth = FirebaseAuth.getInstance()

        viewModelScope.launch {
            if (!email.endsWith("@smvdu.ac.in")) {
                _authResult.value = Result.Error(Exception("Only SMVDU email addresses are allowed"))
                _isLoading.value = false
                return@launch
            }

            if (password != reenterPassword) {
                _authResult.value = Result.Error(Exception("Passwords do not match"))
                _isLoading.value = false
                return@launch
            }

            try {
                // Firebase authentication
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val userEmail = firebaseUser.email ?: ""
                    val name = username

                    // Authenticate with Supabase using same credentials
                    val supabaseAuthSuccess = authenticateWithSupabase(email, password)

                    if (!supabaseAuthSuccess) {
                        Log.w("Auth", "Supabase auth failed, but continuing with Firebase auth")
                    }

                    // Save user data to Supabase
                    withContext(Dispatchers.IO) {
                        saveUserToSupabase(uid, name, userEmail)
                    }

                    _authResult.value = Result.Success(User("", "", ""))
                } else {
                    _authResult.value = Result.Error(Exception("User creation failed"))
                }
            } catch (e: Exception) {
                _authResult.value = Result.Error(e)
                Log.e("FirebaseAuth", "Sign Up Failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Sign out from Firebase
                FirebaseAuth.getInstance().signOut()

                // Sign out from Supabase
                try {
                    supabase.auth.signOut()
                } catch (e: Exception) {
                    Log.e("SupabaseAuth", "Failed to sign out from Supabase: ${e.message}")
                }

                _currentUser.value = null
                _authResult.value = Success(User("", "", ""))
            } catch (e: Exception) {
                _authResult.value = Result.Error(e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            if (!email.endsWith("@smvdu.ac.in")) {
                _authResult.value = Result.Error(Exception("Only SMVDU email addresses are allowed"))
                _isLoading.value = false
                return@launch
            }

            try {
                // Firebase login
                val result = userRepository.login(email, password)

                if (result is Success) {
                    // If Firebase login successful, also authenticate with Supabase
                    val supabaseAuthSuccess = authenticateWithSupabase(email, password)

                    if (!supabaseAuthSuccess) {
                        Log.w("Auth", "Supabase auth failed, but continuing with Firebase auth")
                    }
                }

                _authResult.value = result
            } catch (e: Exception) {
                _authResult.value = Result.Error(e)
                Log.e("AuthViewModel", "Login Failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.email?.contains("@smvdu.ac.in") == true) {
                        user.email?.let { email ->
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    // Generate a secure password for Supabase
                                    val securePassword = generateSecurePassword(user.uid)

                                    // Authenticate with Supabase
                                    val supabaseAuthSuccess = authenticateWithSupabase(email, securePassword)

                                    if (!supabaseAuthSuccess) {
                                        Log.w("Auth", "Supabase auth failed for Google sign-in, continuing with Firebase auth")
                                    }

                                    // Save user to Supabase database
                                    saveUserToSupabase(user.uid, user.displayName ?: "Unknown", email)

                                    withContext(Dispatchers.Main) {
                                        _googleSignInResult.value = user
                                    }
                                } catch (e: Exception) {
                                    Log.e("GoogleSignIn", "Failed to save user data: ${e.message}", e)
                                    withContext(Dispatchers.Main) {
                                        _googleSignInResult.postValue(null)
                                        _authResult.value = Result.Error(Exception("Failed to create user profile: ${e.message}"))
                                    }
                                }
                            }
                        } ?: run {
                            FirebaseAuth.getInstance().signOut()
                            _googleSignInResult.postValue(null)
                            _authResult.value = Result.Error(Exception("Email information missing from Google account"))
                            Log.e("GoogleSignIn", "Email information missing from Google account")
                        }
                    } else {
                        FirebaseAuth.getInstance().signOut()
                        _googleSignInResult.postValue(null)
                        _authResult.value = Result.Error(Exception("Only SMVDU email addresses are allowed"))
                        Log.e("GoogleSignIn", "Non-SMVDU email attempted sign-in: ${user?.email}")
                    }
                } else {
                    val exception = task.exception
                    Log.e("GoogleSignIn", "Sign-in failed: ${exception?.message}", exception)
                    _googleSignInResult.postValue(null)
                    _authResult.value = Result.Error(exception ?: Exception("Google sign-in failed"))
                }
            }
    }

    // Helper function to generate a deterministic password based on UID
    private fun generateSecurePassword(uid: String): String {
        // This creates a consistent password for each user based on their UID
        // You should add a secret salt in a real app
        return uid.hashCode().toString() + "SuperSecret" + uid.takeLast(6)
    }


    private suspend fun authenticateWithSupabase(email: String, password: String): Boolean {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("SupabaseAuth", "Successfully authenticated with Supabase")
            true
        } catch (e: Exception) {
            try {
                // If login fails, try to create the user
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                Log.d("SupabaseAuth", "Successfully created and authenticated with Supabase")
                true
            } catch (e2: Exception) {
                Log.e("SupabaseAuth", "Failed to authenticate with Supabase: ${e2.message}")
                false
            }
        }
    }

}



