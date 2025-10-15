package com.seongho.manageitem.features.main

import android.app.Activity

import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*

import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.R
import com.seongho.manageitem.ui.theme.*
import com.seongho.manageitem.viewmodel.LocalItemVM
import com.seongho.manageitem.viewmodel.MainVM
import com.seongho.manageitem.viewmodel.SettingsVM

data class BottomNavItem(
    val label: String,
    val iconResId: Int,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsScreen(
    mainNavController: NavHostController,
    settingsViewModel: SettingsVM, // Hoisted ViewModel
    mainVM: MainVM,              // Hoisted ViewModel
    localItemVM: LocalItemVM,      // Hoisted ViewModel
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

    val isAuthenticated by mainVM.isUserAuthenticated.collectAsState()

    val items = if (isAuthenticated) {
        listOf(
            BottomNavItem("홈", iconResId = R.drawable.ic_other_house, NavigationDestinations.HOME_SCREEN_TAB),
            BottomNavItem("배치도", iconResId = R.drawable.ic_auto_transmission, NavigationDestinations.LOCATION_SCREEN_TAB),
            BottomNavItem("설정", iconResId = R.drawable.ic_gear, NavigationDestinations.SETTING_SCREEN_TAB)
        )
    } else {
        listOf(
            BottomNavItem("홈", iconResId = R.drawable.ic_other_house, NavigationDestinations.HOME_SCREEN_TAB),
            BottomNavItem("설정", iconResId = R.drawable.ic_gear, NavigationDestinations.SETTING_SCREEN_TAB)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 12.dp)
                    .navigationBarsPadding()
                    .height(80.dp)
                    .shadow(
                        elevation = if (isEffectivelyDarkTheme) 8.dp else 6.dp,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clip(RoundedCornerShape(30.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconResId),
                                contentDescription = item.label,
                                modifier = Modifier.size(30.dp),
                                tint = if (isSelected) MSignature else if (isEffectivelyDarkTheme) Color.White else Color.Black
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                fontSize = 12.sp,
                                color = if (isSelected) MSignature else if (isEffectivelyDarkTheme) Color.White else Color.Black
                            )
                        },
                        selected = isSelected,
                        alwaysShowLabel = false,
                        onClick = {
                            tabNavController.navigate(item.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Transparent
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
                HomeScreen(
                    navController = tabNavController,
                    mainVM = mainVM,
                    localItemVM = localItemVM // HomeScreen으로 전달
                )
            }
            composable(NavigationDestinations.LOCATION_SCREEN_TAB) {
                LocationScreen()
            }
            composable(NavigationDestinations.SETTING_SCREEN_TAB) {
                SettingsScreen(
                    navController = mainNavController,
                    settingsViewModel = settingsViewModel,
                    mainVM = mainVM
                )
            }
            composable(
                route = NavigationDestinations.SEARCHER_SCREEN + "?initialQuery={initialQuery}",
                arguments = listOf(navArgument("initialQuery") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val initialQuery = backStackEntry.arguments?.getString("initialQuery")
                SearcherScreen(
                    navController = tabNavController,
                    mainVM = mainVM,
                    initialQuery = initialQuery
                )
            }
        }
    }
}
