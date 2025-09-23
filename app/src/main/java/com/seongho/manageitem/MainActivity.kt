package com.seongho.manageitem


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.seongho.manageitem.features.entry.AuthScreen
import com.seongho.manageitem.features.main.MainTabsScreen
import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.ui.theme.*
import com.seongho.manageitem.viewmodel.SettingsVM


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsVM = viewModel()

            // ViewModel의 selectedAppTheme 상태 구독
            val userSelectedTheme by settingsViewModel.selectedAppTheme.collectAsState()


            ManageItemTheme(userSelectedTheme = userSelectedTheme) { // 자신의 앱 테마 적용
                AppEntryNavigation()
            }
        }
    }
}

@Composable
fun AppEntryNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationDestinations.AUTH_SCREEN // 시작 화면
    ) {
        composable(NavigationDestinations.AUTH_SCREEN) {
            AuthScreen(navController = navController)
        }
        composable(NavigationDestinations.MAIN_TABS_SCREEN) {
            MainTabsScreen(modifier = Modifier.fillMaxSize())
        }
    }
}
