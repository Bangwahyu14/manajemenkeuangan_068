package com.example.manajemenkeuangan.ui.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.manajemenkeuangan.data.remote.ApiConfig
import com.example.manajemenkeuangan.data.repository.AuthRepository
import com.example.manajemenkeuangan.viewmodel.AuthViewModel
import com.example.manajemenkeuangan.viewmodel.AuthViewModelFactory
import com.example.manajemenkeuangan.databinding.ActivityRegisterBinding
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        val repository = AuthRepository(ApiConfig.getApiService())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }

        viewModel.registerResult.observe(this) { result ->
            val (isSuccess, message) = result
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (isSuccess) {
                finish() // Kembali ke Login setelah sukses
            }
        }
    }

    private fun setupAction() {
        binding.etDateOfBirth.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                binding.etDateOfBirth.setText(date)
            }, year, month, day)
            dpd.show()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi data", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.register(name, email, pass)
            }
        }
    }
}