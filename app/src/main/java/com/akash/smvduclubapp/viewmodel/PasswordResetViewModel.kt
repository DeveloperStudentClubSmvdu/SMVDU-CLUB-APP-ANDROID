package com.akash.smvduclubapp.viewmodel



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.smvduclubapp.data.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PasswordResetViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resetResult = MutableLiveData<Result<Unit>>()
    val resetResult: LiveData<Result<Unit>> = _resetResult

    private val auth = FirebaseAuth.getInstance()

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                auth.sendPasswordResetEmail(email).await()
                _resetResult.postValue(Result.Success(Unit))
            } catch (e: Exception) {
                _resetResult.postValue(Result.Error(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}