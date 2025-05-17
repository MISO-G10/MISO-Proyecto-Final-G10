package com.example.ccpapplication.pages.shopping

import android.content.SharedPreferences

class InMemorySharedPreferences : SharedPreferences {
    private val map = mutableMapOf<String, String>()
    override fun getString(key: String, defValue: String?) = map[key] ?: defValue
    override fun edit() = object : SharedPreferences.Editor {
        override fun putString(key: String, value: String?) = apply {
            if (value != null) map[key] = value
        }
        override fun clear() = apply { map.clear() }
        override fun apply() = Unit
        override fun commit() = true
        // resto de métodos los puedes dejar no soportados o vacíos si no los usas
        override fun remove(key: String) = apply { map.remove(key) }
        override fun putStringSet(key: String, values: MutableSet<String>?) = throw UnsupportedOperationException()
        override fun putInt(key: String, value: Int) = throw UnsupportedOperationException()
        override fun putLong(key: String, value: Long) = throw UnsupportedOperationException()
        override fun putFloat(key: String, value: Float) = throw UnsupportedOperationException()
        override fun putBoolean(key: String, value: Boolean) = throw UnsupportedOperationException()
    }
    // implementa los getters que necesites (getInt, getBoolean, etc.) o lanza UnsupportedOperation
    override fun contains(key: String) = map.containsKey(key)
    override fun getAll() = map.toMap()
    override fun getInt(key: String, defValue: Int) = throw UnsupportedOperationException()
    override fun getLong(key: String, defValue: Long) = throw UnsupportedOperationException()
    override fun getFloat(key: String, defValue: Float) = throw UnsupportedOperationException()
    override fun getBoolean(key: String, defValue: Boolean) = throw UnsupportedOperationException()
    override fun getStringSet(key: String, defValues: MutableSet<String>?) = throw UnsupportedOperationException()
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {}
}
