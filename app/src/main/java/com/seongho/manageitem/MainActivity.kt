package com.seongho.manageitem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.seongho.manageitem.features.entry.AuthScreen
import com.seongho.manageitem.features.main.HomeScreen // 실제 패키지 경로에 맞게 수정
import com.seongho.manageitem.features.main.MainTabsScreen
import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.ui.theme.ManageItemTheme // 자신의 테마로 변경

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManageItemTheme { // 자신의 앱 테마 적용
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
            // 필요하다면 navController를 전달할 수 있음
        }
        // 여기에 나중에 MainTabsScreen과 그 내부 네비게이션이 추가될 것입니다.
    }
}
