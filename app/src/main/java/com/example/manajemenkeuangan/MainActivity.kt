package com.example.manajemenkeuangan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenkeuangan.data.local.SessionManager
import com.example.manajemenkeuangan.ui.admin.dashboard.AdminDashboardActivity
import com.example.manajemenkeuangan.ui.auth.LoginActivity
import com.example.manajemenkeuangan.ui.user.home.HomeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Delay 2 Detik sebelum masuk
        Handler(Looper.getMainLooper()).postDelayed({
            checkSessionAndNavigate()
        }, 2000)
    }

    private fun checkSessionAndNavigate() {
        val sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            val role = sessionManager.getRole()
            if (role == "admin") {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
            } else {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}