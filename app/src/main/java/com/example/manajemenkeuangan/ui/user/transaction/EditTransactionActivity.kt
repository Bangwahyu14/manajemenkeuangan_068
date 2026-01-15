package com.example.manajemenkeuangan.ui.user.transaction

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.manajemenkeuangan.R
import com.example.manajemenkeuangan.data.local.SessionManager
import com.example.manajemenkeuangan.data.model.Transaction
import com.example.manajemenkeuangan.data.remote.ApiConfig
import com.example.manajemenkeuangan.data.repository.TransactionRepository
import com.example.manajemenkeuangan.databinding.ActivityAddEditTransactionBinding
import com.example.manajemenkeuangan.viewmodel.TransactionViewModel
import com.example.manajemenkeuangan.viewmodel.TransactionViewModelFactory
import com.example.manajemenkeuangan.utils.DateHelper // Asumsi Anda punya Helper, jika tidak pakai format manual
import java.util.Calendar

class EditTransactionActivity : AppCompatActivity() {

    // Kita gunakan Layout yang sama dengan Add agar tidak perlu buat XML baru
    private lateinit var binding: ActivityAddEditTransactionBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedType = "pengeluaran"
    private var selectedDate = ""
    private var transactionId: Int? = null
    private var existingTransaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // AMBIL DATA DARI INTENT
        existingTransaction = intent.getSerializableExtra("extra_transaction") as? Transaction

        if (existingTransaction == null) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        transactionId = existingTransaction?.transactionId
        setupUI(existingTransaction!!) // Isi form dengan data lama
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        val repository = TransactionRepository(ApiConfig.getApiService())
        val factory = TransactionViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.saveResult.observe(this) { result ->
            val (success, message) = result
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (success) finish()
        }
    }

    private fun setupUI(data: Transaction) {
        binding.tvPageTitle.text = "Edit Transaksi"
        binding.btnDelete.visibility = View.VISIBLE // TAMPILKAN TOMBOL HAPUS

        val categories = listOf("Makanan", "Transportasi", "Belanja", "Gaji", "Hiburan", "Lainnya")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        binding.etCategory.setAdapter(adapter)

        // Isi Form
        binding.etAmount.setText(data.amount.toString().replace(".0", ""))
        binding.etNote.setText(data.note)
        binding.etCategory.setText(data.categoryName, false)

        selectedDate = data.transactionDate ?: ""
        binding.etDate.setText(selectedDate) // Sesuaikan format tampilan jika perlu

        selectedType = data.type ?: "pengeluaran"
        updateTypeColors()
    }

    private fun setupAction() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnIncome.setOnClickListener {
            selectedType = "pemasukan"
            updateTypeColors()
        }
        binding.btnExpense.setOnClickListener {
            selectedType = "pengeluaran"
            updateTypeColors()
        }

        binding.etDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this, { _, y, m, d ->
                selectedDate = "$y-${m + 1}-$d"
                binding.etDate.setText("$d-${m + 1}-$y")
            }, year, month, day)
            dpd.show()
        }

        // HAPUS DATA
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Transaksi")
                .setMessage("Yakin ingin menghapus?")
                .setPositiveButton("Ya") { _, _ ->
                    transactionId?.let { id -> viewModel.deleteTransaction(id) }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // UPDATE DATA
        binding.btnSave.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()
            val category = binding.etCategory.text.toString()
            val note = binding.etNote.text.toString()

            val transaction = Transaction(
                transactionId = transactionId, // ID PENTING UNTUK UPDATE
                userId = sessionManager.getUserId(),
                amount = amountStr.toDouble(),
                categoryName = category,
                note = note,
                transactionDate = selectedDate,
                type = selectedType
            )

            viewModel.updateTransaction(transaction)
        }
    }

    private fun updateTypeColors() {
        if (selectedType == "pemasukan") {
            binding.btnIncome.setBackgroundResource(R.drawable.bg_input_rounded)
            binding.btnIncome.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.income_green))
            binding.btnIncome.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnExpense.background = null
            binding.btnExpense.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
        } else {
            binding.btnExpense.setBackgroundResource(R.drawable.bg_input_rounded)
            binding.btnExpense.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.expense_red))
            binding.btnExpense.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnIncome.background = null
            binding.btnIncome.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
        }
    }
}