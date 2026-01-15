package com.example.manajemenkeuangan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.manajemenkeuangan.data.model.Transaction
import com.example.manajemenkeuangan.data.repository.TransactionRepository

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveResult = MutableLiveData<Pair<Boolean, String>>()
    val saveResult: LiveData<Pair<Boolean, String>> = _saveResult

    // CREATE
    fun saveTransaction(transaction: Transaction) {
        _isLoading.value = true
        repository.addTransaction(transaction) { success, message ->
            _isLoading.value = false
            _saveResult.value = Pair(success, message)
        }
    }

    // UPDATE
    fun updateTransaction(transaction: Transaction) {
        _isLoading.value = true
        repository.updateTransaction(transaction) { success, message ->
            _isLoading.value = false
            _saveResult.value = Pair(success, message)
        }
    }

    // DELETE
    fun deleteTransaction(transactionId: Int) {
        _isLoading.value = true
        repository.deleteTransaction(transactionId) { success, message ->
            _isLoading.value = false
            _saveResult.value = Pair(success, message)
        }
    }
}

class TransactionViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
