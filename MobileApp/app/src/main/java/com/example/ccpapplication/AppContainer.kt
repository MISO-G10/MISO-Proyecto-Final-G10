package com.example.ccpapplication

import android.content.Context
import android.util.Log
import com.example.ccpapplication.data.repository.ClientRepository
import com.example.ccpapplication.data.repository.ClientRepositoryImpl
import com.example.ccpapplication.data.repository.UserRepository
import com.example.ccpapplication.data.repository.UserRepositoryImpl
import com.example.ccpapplication.data.repository.VisitRepository
import com.example.ccpapplication.data.repository.VisitRepositoryImpl
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
    val visitRepository: VisitRepository
    val clientRepository: ClientRepository
}

class DefaultAppContainer(private val context: Context)  : AppContainer {

    override val tokenManager: TokenManager = SharedPrefsTokenManager(context)

    private val userService: CcpApiServiceAdapter by lazy {
        val baseUrl = BuildConfig.API_URL + BuildConfig.ENDPOINT_USUARIOS
        Log.d("AppContainer", "Creando servicio de usuarios con URL base: $baseUrl")
        RetrofitFactory
            .createRetrofit(baseUrl, tokenManager)
            .create(CcpApiServiceImpl::class.java)
    }

    private val visitService: CcpApiServiceAdapter by lazy {
        val baseUrl = BuildConfig.API_URL + BuildConfig.ENDPOINT_VISITAS
        Log.d("AppContainer", "Creando servicio de visitas con URL base: $baseUrl")
        RetrofitFactory
            .createRetrofit(baseUrl, tokenManager)
            .create(CcpApiServiceImpl::class.java)
    }

    override val userRepository:UserRepository by lazy {
        UserRepositoryImpl(userService,tokenManager)
    }

    override val visitRepository:VisitRepository by lazy {
          VisitRepositoryImpl(visitService,tokenManager)
    }
    
    override val clientRepository:ClientRepository by lazy {
        // Usamos el servicio de visitas ya que el endpoint de tenderos está en ese servicio
        Log.d("AppContainer", "Creando repositorio de clientes usando el servicio de visitas")
        ClientRepositoryImpl(visitService,tokenManager)
    }
}