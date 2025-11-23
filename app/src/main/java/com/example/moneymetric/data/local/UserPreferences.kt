package com.example.moneymetric.data.local

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Ekstensi untuk membuat DataStore
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val INITIAL_CAPITAL_KEY = doublePreferencesKey("initial_capital")
    }

    // Mengambil data modal awal (Flow agar live update)
    val initialCapital: Flow<Double?> = context.dataStore.data.map { preferences ->
        preferences[INITIAL_CAPITAL_KEY]
    }

    // Menyimpan modal awal
    suspend fun setInitialCapital(amount: Double) {
        context.dataStore.edit { preferences ->
            preferences[INITIAL_CAPITAL_KEY] = amount
        }
    }
}