package com.example.softwareganadero.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object OperatorStore {
    private val Context.operatorDataStore: DataStore<Preferences> by preferencesDataStore(name = "operator_store")

    private val OPERATOR_KEY = stringPreferencesKey("operator_name")

    fun flow(context: Context): Flow<String?> {
        return context.operatorDataStore.data
            .map { preferences ->
                preferences[OPERATOR_KEY]
            }
    }

    suspend fun save(context: Context, operator: String) {
        context.operatorDataStore.edit { preferences ->
            preferences[OPERATOR_KEY] = operator
        }
    }

    suspend fun clear(context: Context) {
        context.operatorDataStore.edit { preferences ->
            preferences.remove(OPERATOR_KEY)
        }
    }
}