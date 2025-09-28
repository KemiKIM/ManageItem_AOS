package com.seongho.manageitem.features.main

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // navArgument 임포트

import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.features.main.HomeScreen
import com.seongho.manageitem.features.main.LocationScreen
import com.seongho.manageitem.features.main.SettingScreen
import com.seongho.manageitem.features.main.SearcherScreen

import com.seongho.manageitem.ui.theme.ManageItemTheme

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsScreen(
    mainNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    if (window != null) {
        val isSurfaceLight = MaterialTheme.colorScheme.surface.luminance() > 0.5f
        DisposableEffect(isSurfaceLight, window) {
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = isSurfaceLight
            onDispose {}
        }
    }

    val tabNavController = rememberNavController()

    val items = listOf(
        BottomNavItem("홈", Icons.Filled.Home, NavigationDestinations.HOME_SCREEN_TAB),
        BottomNavItem("배치도", Icons.Filled.ShoppingCart, NavigationDestinations.LOCATION_SCREEN_TAB),
        BottomNavItem("설정", Icons.Filled.Settings, NavigationDestinations.SETTING_SCREEN_TAB)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Green), // TODO: 테마 색상 사용 고려
                ) {
                    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                tabNavController.navigate(item.route) {
                                    popUpTo(tabNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = NavigationDestinations.HOME_SCREEN_TAB,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationDestinations.HOME_SCREEN_TAB) {
                // 1. HomeScreen에 tabNavController 전달
                HomeScreen(navController = tabNavController)
            }
            composable(NavigationDestinations.LOCATION_SCREEN_TAB) {
                LocationScreen()
            }
            composable(NavigationDestinations.SETTING_SCREEN_TAB) {
                SettingScreen(navController = mainNavController) // 설정 화면은 mainNavController 사용 유지
            }

            // 2. SearcherScreen 목적지 추가
            composable(
                route = NavigationDestinations.SEARCHER_SCREEN + "?initialQuery={initialQuery}",
                arguments = listOf(navArgument("initialQuery") {
                    type = androidx.navigation.NavType.StringType // 타입 명시
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val initialQuery = backStackEntry.arguments?.getString("initialQuery")
                // SearcherScreen 호출 시 필요한 모든 파라미터 전달
                // itemViewModel은 SearcherScreen 내부에서 viewModel()로 가져오므로 여기서 전달 안 함
                SearcherScreen(navController = tabNavController, initialQuery = initialQuery)
            }
        }
    }
}
