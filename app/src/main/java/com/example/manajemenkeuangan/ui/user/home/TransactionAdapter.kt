package com.example.manajemenkeuangan.ui.user.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenkeuangan.data.model.Transaction
import com.example.manajemenkeuangan.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

// Ganti ListAdapter jadi RecyclerView.Adapter biasa agar lebih mudah dikontrol manual
class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    // Kita butuh 2 List:
    // 1. listOriginal: Menyimpan SEMUA data asli (Backup)
    // 2. listDisplay: Menyimpan data yang SEDANG DITAMPILKAN (Hasil Filter)
    private var listOriginal = ArrayList<Transaction>()
    private var listDisplay = ArrayList<Transaction>()

    // Event Klik (Callback)
    var onItemClick: ((Transaction) -> Unit)? = null

    // Fungsi untuk memasukkan data awal dari Database
    fun setData(newList: List<Transaction>) {
        listOriginal.clear()
        listOriginal.addAll(newList)

        listDisplay.clear()
        listDisplay.addAll(newList)
        notifyDataSetChanged()
    }

    // --- FITUR FILTER PENCARIAN ---
    fun filter(query: String) {
        val text = query.lowercase(Locale.getDefault())
        listDisplay.clear()

        if (text.isEmpty()) {
            // Jika pencarian kosong, tampilkan semua lagi
            listDisplay.addAll(listOriginal)
        } else {
            // Cari data yang cocok
            for (item in listOriginal) {
                // Kita cari berdasarkan Kategori ATAU Catatan
                val kategori = item.category_name?.lowercase() ?: ""
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
        val transaction = listDisplay[position] // Ambil dari listDisplay
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = listDisplay.size

    inner class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            // Tampilkan data ke layar
            binding.tvCategory.text = transaction.category_name
            binding.tvNote.text = transaction.note
            binding.tvDate.text = transaction.transaction_date

            // Format Rupiah
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            val nominal = formatRupiah.format(transaction.amount ?: 0.0).replace("Rp", "Rp ")
            binding.tvAmount.text = nominal

            // Warna (Hijau/Merah)
            if (transaction.type == "pemasukan") {
                binding.tvAmount.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                binding.tvAmount.text = "+ $nominal"
            } else {
                binding.tvAmount.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                binding.tvAmount.text = "- $nominal"
            }

            // Klik item
            itemView.setOnClickListener {
                onItemClick?.invoke(transaction)
            }
        }
    }
}