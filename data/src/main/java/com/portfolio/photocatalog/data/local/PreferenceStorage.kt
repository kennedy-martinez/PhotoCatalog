package com.portfolio.photocatalog.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferenceStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val LAST_SYNC_KEY = longPreferencesKey("last_sync_timestamp")

    val lastSyncTimestamp: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SYNC_KEY] ?: 0L
        }

    suspend fun updateLastSyncTime(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SYNC_KEY] = timestamp
        }
    }
}