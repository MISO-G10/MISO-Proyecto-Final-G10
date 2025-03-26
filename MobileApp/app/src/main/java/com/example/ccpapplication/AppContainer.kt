package com.example.ccpapplication

import com.example.ccpapplication.data.repository.UserRepository
import com.example.ccpapplication.data.repository.UserRepositoryImpl
import com.example.ccpapplication.services.CcpApiServiceAdapter
import com.example.ccpapplication.services.CcpApiServiceImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val userRepository: UserRepository

}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://api.example.com"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()
    private val retrofitService: CcpApiServiceAdapter by lazy {
        retrofit.create(CcpApiServiceImpl::class.java)

    }

    override val userRepository:UserRepository by lazy {
        UserRepositoryImpl(retrofitService)
    }
}