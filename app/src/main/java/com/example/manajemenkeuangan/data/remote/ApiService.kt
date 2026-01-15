package com.example.manajemenkeuangan.data.remote

import com.example.manajemenkeuangan.data.model.LoginResponse
import com.example.manajemenkeuangan.data.model.Transaction
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Auth
    @FormUrlEncoded
    @POST("login.php")
    fun login(@Field("email") email: String, @Field("password") pass: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun register(@Field("name") name: String, @Field("email") email: String, @Field("password") pass: String): Call<LoginResponse>

    // Transaksi
    @GET("get_transactions.php")
    fun getTransactions(
        @Query("user_id") userId: Int,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Call<List<Transaction>>

    @POST("add_transaction.php")
    fun addTransaction(@Body transaction: Transaction): Call<LoginResponse>

    // --- TAMBAHAN BARU (Pastikan ini ada) ---
    @POST("update_transaction.php")
    fun updateTransaction(@Body transaction: Transaction): Call<LoginResponse>

    @POST("delete_transaction.php")
    fun deleteTransaction(@Body transaction: Transaction): Call<LoginResponse>
    // ----------------------------------------


}