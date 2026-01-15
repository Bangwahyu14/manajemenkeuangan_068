package com.example.manajemenkeuangan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.manajemenkeuangan.data.model.Transaction
import com.example.manajemenkeuangan.data.repository.TransactionRepository

class HomeViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _transactionList = MutableLiveData<List<Transaction>>()
    val transactionList: LiveData<List<Transaction>> = _transactionList

    private val _incomeTotal = MutableLiveData<Double>()
    val incomeTotal: LiveData<Double> = _incomeTotal

    private val _expenseTotal = MutableLiveData<Double>()
    val expenseTotal: LiveData<Double> = _expenseTotal

    private val _balanceTotal = MutableLiveData<Double>()
    val balanceTotal: LiveData<Double> = _balanceTotal

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Update: Menerima parameter month dan year
    fun loadTransactions(userId: Int, month: Int, year: Int) {
        _isLoading.value = true
        repository.getTransactions(userId, month, year) { list, error ->
            _isLoading.value = false
            // Jika list null (error), kita anggap list kosong agar aplikasi tidak crash
            val safeList = list ?: emptyList()

            _transactionList.value = safeList
            calculateSummary(safeList)
        }
    }

    private fun calculateSummary(list: List<Transaction>) {
        var income = 0.0
        var expense = 0.0

        for (transaction in list) {
            // --- BAGIAN INI YANG MEMPERBAIKI ERROR ---
            // Jika amount null, otomatis diganti jadi 0.0 menggunakan elvis operator (?:)
            val amount = transaction.amount ?: 0.0
            // -----------------------------------------

            if (transaction.type == "pemasukan") {
                income += amount
            } else {
                expense += amount
            }
        }

        _incomeTotal.value = income
        _expenseTotal.value = expense
        _balanceTotal.value = income - expense
    }
}