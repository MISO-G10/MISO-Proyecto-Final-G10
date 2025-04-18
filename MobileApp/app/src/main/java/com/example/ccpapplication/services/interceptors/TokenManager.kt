package com.example.ccpapplication.services.interceptors

import android.content.Context
import com.example.ccpapplication.data.model.User
import kotlinx.serialization.json.Json
import androidx.core.content.edit

// services/TokenManager.kt
interface TokenManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun logOut()
    fun saveUser(user:User)
    fun getUser():User?
}

// services/SharedPrefsTokenManager.kt
class SharedPrefsTokenManager(private val context: Context) : TokenManager {
    private val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    override fun saveToken(token: String) {
        sharedPrefs.edit() { putString("auth_token", token) }
    }

    override fun getToken(): String? {
        return sharedPrefs.getString("auth_token", null)
    }


    override fun logOut() {
        sharedPrefs.edit() { remove("auth_token") }
        sharedPrefs.edit() { remove("auth_user") }
    }


    override fun saveUser(user: User) {
        val userJson = json.encodeToString(User.serializer(), user)
        sharedPrefs.edit() {
            putString("auth_user", userJson)
        }
    }

    override fun getUser(): User? {
        val userJson = sharedPrefs.getString("auth_user", null)
        return userJson?.let {
            try {
                json.decodeFromString(User.serializer(), it)
            } catch (e: Exception) {
                null
            }
        }
    }
}