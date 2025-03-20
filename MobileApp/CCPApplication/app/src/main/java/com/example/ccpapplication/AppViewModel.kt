package com.example.ccpapplication

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ccpapplication.App.Companion.updateLocale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.text.compareTo

typealias ChangeLanguage = (String) -> Unit

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "AppViewModel"
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("AppPreferences", Application.MODE_PRIVATE)
    private val languageChangeHelper = LanguageChangeHelper()

    var currentLocale = mutableStateOf(languageChangeHelper.getLanguageCode(application))
        private set

    init {
        loadSavedLocale()
    }

    fun changeLanguage(languageCode: String) {
        Log.d(tag, "changeLanguage called with: $languageCode")
        languageChangeHelper.changeLanguage(getApplication(), languageCode)
        saveLocale(languageCode)
        currentLocale.value = languageCode
    }

    private fun loadSavedLocale() {
        val savedLanguageCode = getSavedLocale()
        Log.d(tag, "loadSavedLocale called, savedLanguageCode: $savedLanguageCode")
        if (savedLanguageCode.isNotEmpty()) {
            Log.d(tag, "loadSavedLocale: Loading saved locale: $savedLanguageCode")
            languageChangeHelper.changeLanguage(getApplication(), savedLanguageCode)
            currentLocale.value = savedLanguageCode

        } else {
            Log.d(tag, "loadSavedLocale: Loading default locale")
            languageChangeHelper.changeLanguage(getApplication(), currentLocale.value)
        }
    }

    private fun saveLocale(locale: String) {
        Log.d(tag, "saveLocale called with: $locale")
        with(sharedPreferences.edit()) {
            putString("saved_locale", locale)
            apply()
        }
    }
    fun getSavedLocale(): String {
        return sharedPreferences.getString("saved_locale", "") ?: ""
    }

    fun setFirstLocale(){
        val savedLanguageCode = getSavedLocale()
        if (savedLanguageCode.isEmpty()){
            changeLanguage("es")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return AppViewModel(Application()) as T
            }
        }
    }
}