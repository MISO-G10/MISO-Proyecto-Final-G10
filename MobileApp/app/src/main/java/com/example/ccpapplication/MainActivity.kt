package com.example.ccpapplication

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import com.example.ccpapplication.navigation.AppNavigation
import com.example.ccpapplication.services.interceptors.TokenManager
import com.example.ccpapplication.ui.theme.AppTheme

class MainActivity : AppCompatActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private val tokenManager: TokenManager by lazy {
        App.instance.container.tokenManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel.setFirstLocale()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            )
        )

        setContent {
            var currentLocale by remember { mutableStateOf(appViewModel.currentLocale.value) }
            currentLocale = appViewModel.currentLocale.value
            val configuration = LocalConfiguration.current
            configuration.setLocale(java.util.Locale(currentLocale))
            AppTheme {
                AppNavigation(appViewModel =appViewModel, tokenManager = tokenManager )
            }
        }
    }
}