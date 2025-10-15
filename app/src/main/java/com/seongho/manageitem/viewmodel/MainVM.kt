package com.seongho.manageitem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seongho.manageitem.utils.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainVM(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application.applicationContext)

    private var authCheckPerformed = false

    val isUserAuthenticated: StateFlow<Boolean> = userPreferencesRepository.isUserAuthenticated
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val storedAuthCode: StateFlow<String?> = userPreferencesRepository.storedAuthCode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 인증 상태와 코드를 한번에 저장
    fun saveAuthentication(code: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveAuthentication(code)
        }
    }

    // 인증 상태와 코드를 한번에 삭제
    fun clearAuthentication() {
        viewModelScope.launch {
            userPreferencesRepository.clearAuthentication()
        }
    }

    fun needsAuthCheck(): Boolean = !authCheckPerformed

    fun markAuthCheckPerformed() {
        authCheckPerformed = true
    }
}