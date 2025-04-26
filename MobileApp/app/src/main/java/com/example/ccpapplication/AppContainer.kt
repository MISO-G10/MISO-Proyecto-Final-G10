package com.example.ccpapplication

import android.content.Context
import com.example.ccpapplication.data.repository.UserRepository
import com.example.ccpapplication.data.repository.UserRepositoryImpl
import com.example.ccpapplication.services.CcpApiServiceAdapter
import com.example.ccpapplication.services.CcpApiServiceImpl
import com.example.ccpapplication.services.RetrofitFactory
import com.example.ccpapplication.services.interceptors.AuthInterceptor
import com.example.ccpapplication.services.interceptors.SharedPrefsTokenManager
import com.example.ccpapplication.services.interceptors.TokenManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


interface AppContainer {
    val userRepository: UserRepository
    val tokenManager: TokenManager
}

class DefaultAppContainer(private val context: Context)  : AppContainer {

    override val tokenManager: TokenManager = SharedPrefsTokenManager(context)



    private val userService: CcpApiServiceAdapter by lazy {
        RetrofitFactory
            .createRetrofit(BuildConfig.API_URL+BuildConfig.ENDPOINT_USUARIOS, tokenManager)
            .create(CcpApiServiceImpl::class.java)

    }

    private val visitService: CcpApiServiceAdapter by lazy {
        RetrofitFactory
            .createRetrofit(BuildConfig.API_URL+BuildConfig.ENDPOINT_VISITAS, tokenManager)
            .create(CcpApiServiceImpl::class.java)

    }

    override val userRepository:UserRepository by lazy {
        UserRepositoryImpl(userService,tokenManager)
    }

    /*override val visitRepository:VisitRepository by lazy {
        VisitRepositoryImpl(visitService,tokenManager)
    }*/

}