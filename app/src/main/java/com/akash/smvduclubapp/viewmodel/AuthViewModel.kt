package com.akash.smvduclubapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.smvduclubapp.Injection
import com.akash.smvduclubapp.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.akash.smvduclubapp.data.Result
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider



class AuthViewModel : ViewModel() {
    private val userRepository: UserRepository

    init {
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    private val _googleSignInResult = MutableLiveData<FirebaseUser?>()
    val googleSignInResult: LiveData<FirebaseUser?> get() = _googleSignInResult

    // Check if the user is authenticated
    fun isUserAuthenticated(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun signUp(email: String, password: String, username: String, reenterPassword: String) {
        viewModelScope.launch {
            if (!email.endsWith("@smvdu.ac.in")) {
                _authResult.value = Result.Error(Exception("Only SMVDU email addresses are allowed"))
                return@launch
            }

            if (password != reenterPassword) {
                _authResult.value = Result.Error(Exception("Passwords do not match"))
                return@launch
            }

            val result = userRepository.signUp(email, password, username, reenterPassword)
            _authResult.value = result
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (!email.endsWith("@smvdu.ac.in")) {
                _authResult.value = Result.Error(Exception("Only SMVDU email addresses are allowed"))
                return@launch
            }

            _authResult.value = userRepository.login(email, password)
        }
    }

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.email?.endsWith("@smvdu.ac.in") == true) {
                        _googleSignInResult.value = user
                    } else {
                        FirebaseAuth.getInstance().signOut() // Sign out if email is not from SMVDU
                        _googleSignInResult.value = null
                    }
                } else {
                    _googleSignInResult.value = null
                }
            }
    }
}

