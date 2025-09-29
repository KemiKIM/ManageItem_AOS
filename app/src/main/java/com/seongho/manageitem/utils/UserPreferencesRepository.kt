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

    private val AUTH_PIN = "12345"

    // 저장할 데이터의 키를 정의
    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val AUTH_PIN = stringPreferencesKey("auth_pin")
        val IS_USER_AUTHENTICATED = booleanPreferencesKey("is_user_authenticated")
    }




    // 1. APP_THEME : '현재 선택된 테마'를 Flow로 읽어옴
    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            // 저장된 문자열 값을 AppTheme enum으로 변환, 없으면 SYSTEM 기본값
            val themeName = preferences[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                // 저장된 값이 유효하지 않은 enum 이름일 경우 기본값 반환
                AppTheme.SYSTEM
            }
        }


    // 1. APP_THEME : '선택된 테마'를 DataStore에 저장
    suspend fun saveAppTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme.name // enum의 이름을 문자열로 저장
        }
    }






    fun isValidAuthPin(inputPin: String): Boolean {
        return inputPin == AUTH_PIN
    }



    // --- 사용자 인증 상태 읽기 ---
    val isUserAuthenticated: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_USER_AUTHENTICATED] ?: false // 기본값 false
        }

    // --- 사용자 인증 상태 저장 ---
    suspend fun updateUserAuthenticationState(isAuthenticated: Boolean) {
        Log.d("UserPreferencesRepo", "Updating IS_USER_AUTHENTICATED to: $isAuthenticated")
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_USER_AUTHENTICATED] = isAuthenticated
        }
    }
}