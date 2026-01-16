package com.example.manajemenkeuangan.ui.user.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenkeuangan.data.model.Transaction
import com.example.manajemenkeuangan.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var listOriginal = ArrayList<Transaction>()
    private var listDisplay = ArrayList<Transaction>()

    var onItemClick: ((Transaction) -> Unit)? = null

    fun setData(newList: List<Transaction>) {
        listOriginal.clear()
        listOriginal.addAll(newList)

        listDisplay.clear()
        listDisplay.addAll(newList)
        notifyDataSetChanged()
    }

    // --- LOGIKA PENCARIAN ---
    fun filter(query: String) {
        val text = query.lowercase(Locale.getDefault())
        listDisplay.clear()

        if (text.isEmpty()) {
            listDisplay.addAll(listOriginal)
        } else {
            for (item in listOriginal) {
                val kategori = item.categoryName?.lowercase() ?: ""
                val catatan = item.note?.lowercase() ?: ""

                if (kategori.contains(text) || catatan.contains(text)) {
                    listDisplay.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = listDisplay[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = listDisplay.size

    inner class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.tvCategory.text = transaction.categoryName
            binding.tvNote.text = transaction.note
            binding.tvDate.text = transaction.transactionDate

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            val nominal = formatRupiah.format(transaction.amount ?: 0.0).replace("Rp", "Rp ")
            binding.tvAmount.text = nominal

            if (transaction.type == "pemasukan") {
                binding.tvAmount.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                binding.tvAmount.text = "+ $nominal"
            } else {
                binding.tvAmount.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                binding.tvAmount.text = "- $nominal"
            }

            itemView.setOnClickListener {
                onItemClick?.invoke(transaction)
            }
        }
    }
}