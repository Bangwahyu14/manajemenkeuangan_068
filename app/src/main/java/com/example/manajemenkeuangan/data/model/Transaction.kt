package com.example.manajemenkeuangan.data.model // <--- PASTIKAN INI BENAR

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Transaction(
    @SerializedName("transaction_id")
    val transactionId: Int? = null,

    @SerializedName("user_id")
    val userId: Int? = 0,

    @SerializedName("wallet_id")
    val walletId: Int? = 0,

    @SerializedName("category_id")
    val categoryId: Int? = 0,

    @SerializedName("category_name")
    val categoryName: String? = "Lainnya",

    @SerializedName("amount")
    val amount: Double? = 0.0,

    @SerializedName("note")
    val note: String? = "",

    @SerializedName("transaction_date")
    val transactionDate: String? = "",

    @SerializedName("type")
    val type: String? = "pengeluaran"
) : Serializable