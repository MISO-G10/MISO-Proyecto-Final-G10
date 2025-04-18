package com.example.ccpapplication.services

import android.util.Log
import com.example.ccpapplication.services.interceptors.AuthInterceptor
import com.example.ccpapplication.services.interceptors.TokenManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitFactory {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun createRetrofit(baseUrl: String, tokenManager: TokenManager): Retrofit {
        Log.d("UserRepository", "Trying login with: $baseUrl")
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
