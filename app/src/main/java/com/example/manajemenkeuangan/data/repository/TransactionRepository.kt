package com.example.manajemenkeuangan.data.repository


import com.example.manajemenkeuangan.data.model.LoginResponse
import com.example.manajemenkeuangan.data.model.Transaction
import com.example.manajemenkeuangan.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionRepository(private val apiService: ApiService) {

    fun getTransactions(userId: Int, month: Int, year: Int, onResult: (List<Transaction>?, String?) -> Unit) {
        apiService.getTransactions(userId, month, year).enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Gagal memuat data")
                }
            }
            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                onResult(null, t.message)
            }
        })
    }

    fun addTransaction(transaction: Transaction, onResult: (Boolean, String) -> Unit) {
        apiService.addTransaction(transaction).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Gagal menyimpan data")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(false, t.message ?: "Error koneksi")
            }
        })
    }

    // --- TAMBAHAN BARU YANG HILANG ---
    fun updateTransaction(transaction: Transaction, onResult: (Boolean, String) -> Unit) {
        apiService.updateTransaction(transaction).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Gagal Update")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(false, t.message ?: "Error Koneksi")
            }
        })
    }

    fun deleteTransaction(transactionId: Int, onResult: (Boolean, String) -> Unit) {
        // Bungkus ID ke dalam object Transaction karena API butuh Body JSON
        val transaction = Transaction(transactionId = transactionId)

        apiService.deleteTransaction(transaction).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Gagal Hapus")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(false, t.message ?: "Error Koneksi")
            }
        })
    }
}