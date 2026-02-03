package com.example.pomodoro.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pomodoro_settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val BACKGROUND_INDEX_KEY = intPreferencesKey("background_index")
        private val LAST_MODE_KEY = intPreferencesKey("last_mode")
        private val CUSTOM_BG_URI_KEY = stringPreferencesKey("custom_bg_uri")
        private val POWER_SAVE_KEY = booleanPreferencesKey("power_save_mode")
    }
    
    val backgroundIndex: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[BACKGROUND_INDEX_KEY] ?: 0
    }
    
    val lastMode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LAST_MODE_KEY] ?: 0
    }
    
    val customBackgroundUri: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[CUSTOM_BG_URI_KEY]
    }
    
    val powerSaveMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[POWER_SAVE_KEY] ?: false
    }
    
    suspend fun saveBackgroundIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[BACKGROUND_INDEX_KEY] = index
        }
    }
    
    suspend fun saveLastMode(modeOrdinal: Int) {
        context.dataStore.edit { preferences ->
            preferences[LAST_MODE_KEY] = modeOrdinal
        }
    }
    
    suspend fun saveCustomBackgroundUri(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri != null) {
                preferences[CUSTOM_BG_URI_KEY] = uri
            } else {
                preferences.remove(CUSTOM_BG_URI_KEY)
            }
        }
    }
    
    suspend fun savePowerSaveMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[POWER_SAVE_KEY] = enabled
        }
    }
}
