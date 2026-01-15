package com.example.manajemenkeuangan.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyHelper {
    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("Rp", "Rp ").substringBeforeLast(",00")
    }
}