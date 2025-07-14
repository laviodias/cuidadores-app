package com.example.cuidadores.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    
    companion object {
        private const val PREF_NAME = "CuidadoresSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_USER_NAME = "userName"
    }
    
    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    fun criarSessao(userId: Long, email: String, nome: String) {
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_USER_ID, userId)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_NAME, nome)
        editor.apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getUserId(): Long {
        return sharedPref.getLong(KEY_USER_ID, -1L)
    }
    
    fun getUserEmail(): String? {
        return sharedPref.getString(KEY_USER_EMAIL, null)
    }
    
    fun getUserName(): String? {
        return sharedPref.getString(KEY_USER_NAME, null)
    }
    
    fun logout() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
    
    fun limparSessao() {
        logout()
    }
} 