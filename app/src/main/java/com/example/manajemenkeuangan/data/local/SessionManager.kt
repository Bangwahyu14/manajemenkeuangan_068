package com.example.manajemenkeuangan.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREF_NAME = "user_session"
        const val KEY_IS_LOGIN = "is_login"
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "user_id"
        const val KEY_NAME = "name"
        const val KEY_ROLE = "role"
    }

    fun saveSession(token: String, userId: Int, name: String, role: String) {
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGIN, true)
        editor.putString(KEY_TOKEN, token)
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGIN, false)

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getRole(): String? = prefs.getString(KEY_ROLE, "user")

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)

    // TAMBAHAN: Fungsi ini memperbaiki error di HomeActivity
    fun getUserName(): String {
        return prefs.getString(KEY_NAME, "User") ?: "User"
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}