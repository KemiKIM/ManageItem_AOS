package com.seongho.manageitem.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.seongho.manageitem.utils.UserPreferencesRepository
import com.seongho.manageitem.ui.theme.AppTheme


class SettingsVM(application: Application) : AndroidViewModel(application) {

    // UserPreferencesRepository 인스턴스 생성
    private val userPreferencesRepository = UserPreferencesRepository(application.applicationContext)

    // DataStore에서 테마 설정을 읽어 StateFlow로 변환
    // 앱이 활성화되어 있을 때만 구독하고, 초기값은 AppTheme.SYSTEM으로 설정
    val selectedAppTheme: StateFlow<AppTheme> = userPreferencesRepository.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // 5초 동안 구독자가 없으면 Flow 중단
            initialValue = AppTheme.SYSTEM // 초기값 (Flow가 처음 값을 방출하기 전까지 사용될 값)
        )

    var modeSettingExpanded by mutableStateOf(false)
        private set

    // Screen White/Dark
    fun onModeSettingExpanded(isExpanded: Boolean) {
        modeSettingExpanded = isExpanded
    }

    fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.saveAppTheme(theme)
        }
    }
}