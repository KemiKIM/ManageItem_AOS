package com.seongho.manageitem.features.main

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.*
import androidx.core.view.WindowCompat

import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.features.main.HomeScreen
import com.seongho.manageitem.features.main.LocationScreen
import com.seongho.manageitem.features.main.SettingScreen
import com.seongho.manageitem.features.main.SearcherScreen

import com.seongho.manageitem.ui.theme.*

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
    val isEffectivelyDarkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val items = listOf(
        BottomNavItem("홈", Icons.Filled.Home, NavigationDestinations.HOME_SCREEN_TAB),
//        BottomNavItem("배치도", Icons.Default.TurnSharpRight, NavigationDestinations.LOCATION_SCREEN_TAB),
        BottomNavItem("설정", Icons.Filled.SettingsSuggest, NavigationDestinations.SETTING_SCREEN_TAB)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {

                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .height(80.dp)
                        .shadow(
                            elevation = if (isEffectivelyDarkTheme) 8.dp else 6.dp, // 다크 모드에서 elevation 증가
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clip(RoundedCornerShape(30.dp)),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination


                    items.forEach { item ->
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.route == item.route } == true

                        NavigationBarItem(
                            icon = {
                                Box(
                                    modifier = Modifier.padding(top = 25.dp)
                                ) {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(30.dp)

                                    )
                                }

                            },
                            label = {
                                if (isSelected) {
                                    Text(
                                        item.label,
                                        fontSize = 12.sp
                                    )
                                }
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                tabNavController.navigate(item.route) {
                                    popUpTo(tabNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MSignature,
                                selectedTextColor = MSignature,
                                unselectedIconColor = if (isEffectivelyDarkTheme) Color.White else Color.Black,
                                unselectedTextColor = if (isEffectivelyDarkTheme) Color.White else Color.Black,
                                indicatorColor = Transparent // 선택 표시기 배경색 (투명하게 하거나 surfaceVariant와 동일하게 할 수도 있음)
                            )
                        )
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
