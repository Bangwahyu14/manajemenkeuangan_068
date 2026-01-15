package com.example.manajemenkeuangan.data.repository

import com.example.manajemenkeuangan.data.model.LoginResponse
import com.example.manajemenkeuangan.data.model.UserData
import com.example.manajemenkeuangan.data.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    fun login(email: String, pass: String, onResult: (Boolean, String, UserData?) -> Unit) {
        apiService.login(email, pass).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        onResult(true, body.message, body.data)
                    } else {
                        onResult(false, body.message, null)
                    }
                } else {
                    onResult(false, "Terjadi kesalahan server", null)
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(false, t.message ?: "Koneksi gagal", null)
            }
        })
    }

    fun register(name: String, email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        apiService.register(name, email, pass).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body()!!.success, response.body()!!.message)
                } else {
                    onResult(false, "Register gagal: " + response.message())
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(false, t.message ?: "Koneksi gagal")
            }
        })
    }
}