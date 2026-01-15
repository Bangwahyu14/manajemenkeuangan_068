package com.example.manajemenkeuangan.ui.user.transaction


import com.example.manajemenkeuangan.data.model.Transaction
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.SurfaceControl
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.manajemenkeuangan.data.local.SessionManager
import com.example.manajemenkeuangan.data.remote.ApiConfig
import com.example.manajemenkeuangan.data.repository.TransactionRepository
import com.example.manajemenkeuangan.viewmodel.TransactionViewModel
import com.example.manajemenkeuangan.viewmodel.TransactionViewModelFactory
import com.example.manajemenkeuangan.R
import com.example.manajemenkeuangan.databinding.ActivityAddEditTransactionBinding
import java.util.Calendar

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTransactionBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedType = "pengeluaran" // Default
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupViewModel()
        setupUI()
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
            if (success) {
                finish() // Tutup halaman ini dan kembali ke Home
            }
        }
    }

    private fun setupUI() {
        // Setup Spinner Kategori
        val categories = listOf("Makanan", "Transportasi", "Belanja", "Gaji", "Hiburan", "Lainnya")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        binding.etCategory.setAdapter(adapter)

        // Set Default Date Hari Ini
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        selectedDate = "$year-$month-$day" // Format API: YYYY-MM-DD
        binding.etDate.setText("$day-$month-$year") // Format Tampilan

        updateTypeColors()
    }

    private fun setupAction() {
        binding.btnBack.setOnClickListener { finish() }

        // Pilihan Tipe
        binding.btnIncome.setOnClickListener {
            selectedType = "pemasukan"
            updateTypeColors()
        }
        binding.btnExpense.setOnClickListener {
            selectedType = "pengeluaran"
            updateTypeColors()
        }

        // Date Picker
        binding.etDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, y, m, d ->
                selectedDate = "$y-${m + 1}-$d" // Format Database
                binding.etDate.setText("$d-${m + 1}-$y") // Format Tampilan
            }, year, month, day)
            dpd.show()
        }

        // Simpan
        binding.btnSave.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()
            val category = binding.etCategory.text.toString()
            val note = binding.etNote.text.toString()

            if (amountStr.isEmpty()) {
                binding.etAmount.error = "Nominal harus diisi"
                return@setOnClickListener
            }

            val amount = amountStr.toDouble()
            val userId = sessionManager.getUserId()

            // Buat Object Transaction
            // INI BENAR
            val transaction = Transaction(
                walletId = 1, // Default wallet 1
                userId = userId,
                categoryId = 1, // Default category ID
                categoryName = category,
                amount = amount,
                note = note,
                transactionDate = selectedDate,
                type = selectedType
            )

            viewModel.saveTransaction(transaction)
        }
    }

    private fun updateTypeColors() {
        if (selectedType == "pemasukan") {
            // Aktifkan Income (Biru/Hijau)
            binding.btnIncome.setBackgroundResource(R.drawable.bg_input_rounded)
            binding.btnIncome.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.income_green))
            binding.btnIncome.setTextColor(ContextCompat.getColor(this, R.color.white))

            // Matikan Expense
            binding.btnExpense.background = null
            binding.btnExpense.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
        } else {
            // Aktifkan Expense (Merah)
            binding.btnExpense.setBackgroundResource(R.drawable.bg_input_rounded)
            binding.btnExpense.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.expense_red))
            binding.btnExpense.setTextColor(ContextCompat.getColor(this, R.color.white))

            // Matikan Income
            binding.btnIncome.background = null
            binding.btnIncome.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
        }
    }
}