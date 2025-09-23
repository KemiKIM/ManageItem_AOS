package com.seongho.manageitem.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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




    // DataStore에서 인증화면 사용 여부를 읽어 StateFlow로 변환
    // userPreferencesRepository에 authScreenEnabled Flow가 정의되어 있다고 가정
    val authScreenEnabled: StateFlow<Boolean> = userPreferencesRepository.authScreenEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true // DataStore에서 값을 불러오기 전까지의 기본값
        )

    var modeSettingExpanded by mutableStateOf(false)
        private set





    // Auth 상태 변경 및 DataStore에 저장
    fun onAuthScreenToggled(isEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveAuthScreenEnabled(isEnabled)
            // authScreenEnabled StateFlow는 userPreferencesRepository.authScreenEnabled를 구독하므로
            // DataStore에 저장되면 자동으로 UI에 반영됩니다.
            // 따라서 여기서 ViewModel 내부의 상태를 직접 변경할 필요가 없어집니다.
        }
    }



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