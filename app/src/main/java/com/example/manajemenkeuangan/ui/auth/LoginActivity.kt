package com.example.manajemenkeuangan.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.manajemenkeuangan.data.local.SessionManager
import com.example.manajemenkeuangan.data.remote.ApiConfig
import com.example.manajemenkeuangan.data.repository.AuthRepository
import com.example.manajemenkeuangan.ui.admin.dashboard.AdminDashboardActivity
import com.example.manajemenkeuangan.ui.user.home.HomeActivity
import com.example.manajemenkeuangan.viewmodel.AuthViewModel
import com.example.manajemenkeuangan.viewmodel.AuthViewModelFactory
import com.example.manajemenkeuangan.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToDashboard(sessionManager.getRole())
        }

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        val repository = AuthRepository(ApiConfig.getApiService())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        viewModel.loginResult.observe(this) { result ->
            val (isSuccess, message) = result
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.userData.observe(this) { user ->
            if (user != null) {
                sessionManager.saveSession(user.token, user.userId, user.name, user.role)
                navigateToDashboard(user.role)
            }
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(email, password)
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToDashboard(role: String?) {
        val intent = if (role == "admin") {
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Intent(this, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}