package com.example.manajemenkeuangan.ui.user.home

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manajemenkeuangan.data.local.SessionManager
import com.example.manajemenkeuangan.data.remote.ApiConfig
import com.example.manajemenkeuangan.data.repository.TransactionRepository
import com.example.manajemenkeuangan.databinding.ActivityHomeBinding
import com.example.manajemenkeuangan.ui.auth.LoginActivity
import com.example.manajemenkeuangan.ui.user.transaction.AddTransactionActivity
import com.example.manajemenkeuangan.ui.user.transaction.EditTransactionActivity
import com.example.manajemenkeuangan.viewmodel.HomeViewModel
import com.example.manajemenkeuangan.viewmodel.HomeViewModelFactory
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: TransactionAdapter

    // Variabel Filter
    private var currentMonth = 0
    private var currentYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            moveToLogin()
            return
        }

        val userName = sessionManager.getUserName()
        binding.tvWelcome.text = "Halo, $userName"

        // Set Default Waktu ke Bulan Ini
        val calendar = Calendar.getInstance()
        currentMonth = calendar.get(Calendar.MONTH) + 1
        currentYear = calendar.get(Calendar.YEAR)

        setupTextFilter()
        setupViewModel()
        setupRecyclerView()
        setupActions()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val userId = sessionManager.getUserId()
        viewModel.loadTransactions(userId, currentMonth, currentYear)
    }

    private fun setupViewModel() {
        val repository = TransactionRepository(ApiConfig.getApiService())
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // Observasi Data
        viewModel.transactionList.observe(this) { list ->
            adapter.setData(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.incomeTotal.observe(this) { income ->
            binding.tvIncome.text = formatRupiah(income)
        }
        viewModel.expenseTotal.observe(this) { expense ->
            binding.tvExpense.text = formatRupiah(expense)
        }
        viewModel.balanceTotal.observe(this) { balance ->
            binding.tvBalance.text = formatRupiah(balance)
        }

        loadData()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter()
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = adapter

        adapter.onItemClick = { transaction ->
            val intent = Intent(this, EditTransactionActivity::class.java)
            intent.putExtra("extra_transaction", transaction)
            startActivity(intent)
        }
    }

    private fun setupActions() {
        binding.ivLogout.setOnClickListener {
            sessionManager.logout()
            moveToLogin()
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        binding.tvViewGraph.setOnClickListener {
            val intent = Intent(this, com.example.manajemenkeuangan.ui.user.chart.ChartActivity::class.java)
            intent.putExtra("extra_month", currentMonth)
            intent.putExtra("extra_year", currentYear)
            startActivity(intent)
        }

        // Filter Action
        binding.btnFilterDate.setOnClickListener { showDatePicker() }
        binding.tvCurrentFilter.setOnClickListener { showDatePicker() }

        binding.svSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Saat user mengetik, panggil fungsi filter di adapter
                adapter.filter(newText ?: "")
                return true
            }
        })
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, _ ->
                currentMonth = month + 1
                currentYear = year
                setupTextFilter()
                loadData()
            },
            currentYear,
            currentMonth - 1,
            1
        )
        datePickerDialog.show()
    }

    private fun setupTextFilter() {
        val monthName = java.text.DateFormatSymbols().shortMonths[currentMonth - 1]
        binding.tvCurrentFilter.text = "$monthName $currentYear"
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number).replace("Rp", "Rp ")
    }
}