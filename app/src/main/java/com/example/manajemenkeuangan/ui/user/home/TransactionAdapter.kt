package com.example.manajemenkeuangan.ui.user.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter // <--- PENTING: Pakai ListAdapter, bukan RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenkeuangan.R
import com.example.manajemenkeuangan.data.model.Transaction
import com.example.manajemenkeuangan.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

// Perhatikan: class ini sekarang extend 'ListAdapter', bukan 'RecyclerView.Adapter'
class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DiffCallback) {

    // Variable untuk menangani klik item (dikirim ke Activity)
    var onItemClick: ((Transaction) -> Unit)? = null

    // DiffUtil: Membandingkan data lama vs baru agar update efisien
    companion object DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            // Membandingkan apakah ID-nya sama
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            // Membandingkan apakah seluruh isinya sama
            return oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            // Set Data ke Text View
            binding.tvCategory.text = transaction.categoryName
            binding.tvNote.text = if (transaction.note.isNullOrEmpty()) "-" else transaction.note
            binding.tvDate.text = transaction.transactionDate

            // Format Rupiah
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            val formattedAmount = formatRupiah.format(transaction.amount ?: 0.0).replace("Rp", "Rp ")

            binding.tvAmount.text = formattedAmount

            // Logika Warna: Pemasukan (Hijau), Pengeluaran (Merah)
            if (transaction.type == "pemasukan") {
                binding.tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.income_green))
            } else {
                binding.tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.expense_red))
            }

            // Aksi Klik pada item
            itemView.setOnClickListener {
                onItemClick?.invoke(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = getItem(position) // getItem() adalah method bawaan ListAdapter
        holder.bind(transaction)
    }
}