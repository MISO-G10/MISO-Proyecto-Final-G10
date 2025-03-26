package com.example.ccpapplication

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import java.util.Locale

class App: Application(

) {
    /** instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    companion object {
        lateinit var instance: App private set
        fun updateLocale(context: Context, locale: Locale) {
            val resources = context.resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        container = DefaultAppContainer()
        updateLocale(this, Locale("es"))
    }

}

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringRes, *formatArgs)
    }
}
