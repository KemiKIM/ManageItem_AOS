package com.seongho.manageitem.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.seongho.manageitem.ui.theme.*

// Context의 확장 프로퍼티로 DataStore 인스턴스 생성
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    // 저장할 데이터의 키를 정의
    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val IS_USER_AUTHENTICATED = booleanPreferencesKey("is_user_authenticated")
        val STORED_AUTH_CODE = stringPreferencesKey("stored_auth_code")
    }

    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map {
            val themeName = it[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                AppTheme.SYSTEM
            }
        }

    suspend fun saveAppTheme(theme: AppTheme) {
        context.dataStore.edit { it[PreferencesKeys.APP_THEME] = theme.name }
    }

    val isUserAuthenticated: Flow<Boolean> = context.dataStore.data
        .map { it[PreferencesKeys.IS_USER_AUTHENTICATED] ?: false }

    val storedAuthCode: Flow<String?> = context.dataStore.data
        .map { it[PreferencesKeys.STORED_AUTH_CODE] }

    suspend fun saveAuthentication(code: String) {
        context.dataStore.edit {
            it[PreferencesKeys.IS_USER_AUTHENTICATED] = true
            it[PreferencesKeys.STORED_AUTH_CODE] = code
        }
    }

    suspend fun clearAuthentication() {
        context.dataStore.edit {
            it[PreferencesKeys.IS_USER_AUTHENTICATED] = false
            it.remove(PreferencesKeys.STORED_AUTH_CODE)
        }
    }
}