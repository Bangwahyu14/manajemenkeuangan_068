package com.example.manajemenkeuangan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.manajemenkeuangan.data.model.UserData
import com.example.manajemenkeuangan.data.repository.AuthRepository


class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Pair<Boolean, String>>()
    val loginResult: LiveData<Pair<Boolean, String>> = _loginResult

    private val _registerResult = MutableLiveData<Pair<Boolean, String>>()
    val registerResult: LiveData<Pair<Boolean, String>> = _registerResult

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData

    fun login(email: String, pass: String) {
        _isLoading.value = true
        repository.login(email, pass) { success, message, data ->
            _isLoading.value = false
            _loginResult.value = Pair(success, message)
            if (success) _userData.value = data
        }
    }

    fun register(name: String, email: String, pass: String) {
        _isLoading.value = true
        repository.register(name, email, pass) { success, message ->
            _isLoading.value = false
            _registerResult.value = Pair(success, message)
        }
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}