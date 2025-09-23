package com.seongho.manageitem.utils

import android.content.Context
import androidx.datastore.core.DataStore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import java.io.IOException // map 연산자 내에서 예외 처리를 위해 추가 (선택적이지만 권장)
import androidx.datastore.preferences.core.emptyPreferences // 초기값 또는 에러 시 사용

import com.seongho.manageitem.ui.theme.*



// Context의 확장 프로퍼티로 DataStore 인스턴스 생성
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    // 저장할 데이터의 키를 정의
    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val AUTH_SCREEN_ENABLED = booleanPreferencesKey("auth_screen_enabled") // 인증화면 키 추가
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

    // 2. AUTH_SCREEN_ENABLED : '인증화면 사용 여부'를 Flow로 읽어옴
    val authScreenEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            // 키가 없으면 기본값으로 true를 반환 (인증화면 기본 활성화)
            preferences[PreferencesKeys.AUTH_SCREEN_ENABLED] ?: true
        }

    // 2. AUTH_SCREEN_ENABLED : '인증화면 사용 여부'를 DataStore에 저장
    suspend fun saveAuthScreenEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_SCREEN_ENABLED] = isEnabled
        }
    }


}