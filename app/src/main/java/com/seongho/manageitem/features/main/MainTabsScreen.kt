package com.seongho.manageitem.features.main // 실제 패키지 경로에 맞게 수정

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seongho.manageitem.features.main.HomeScreen // HomeScreen 임포트
import com.seongho.manageitem.features.main.LocationScreen // SecondTabScreen 임포트
import com.seongho.manageitem.features.main.SettingScreen // ThirdTabScreen 임포트
import com.seongho.manageitem.navigation.NavigationDestinations // 경로 정의 임포트

// 탭 정보를 담는 데이터 클래스 (이전에 정의했을 수 있음)
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsScreen(
    // mainNavController: NavHostController, // 전체 앱 네비게이션용 (필요하다면)
    modifier: Modifier = Modifier
) {
    val tabNavController = rememberNavController() // 탭 내부 화면 전환용 NavController

    // 하단 탭 아이템 정의
    val items = listOf(
        BottomNavItem("홈", Icons.Filled.Home, NavigationDestinations.HOME_SCREEN_TAB), // 경로 이름은 일관성 있게
        BottomNavItem("배치도", Icons.Filled.ShoppingCart, NavigationDestinations.LOCATION_SCREEN_TAB),
        BottomNavItem("설정", Icons.Filled.Settings, NavigationDestinations.SETTING_SCREEN_TAB)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()
                .background(Color.Yellow)
            ) { // Column으로 묶어서 NavigationBar와 광고 영역 배치
                // 1. 하단 네비게이션 바
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth() // 너비는 꽉 채우도록
                        .height(56.dp)
                        .background(Color.Green)
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

                // 2. 광고 영역 (NavigationBar 바로 아래)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.LightGray) // 광고가 로드되기 전까지 시각적으로 영역 표시 (선택 사항)
                ) {
                    // TODO: 여기에 실제 Google AdMob 배너 광고 Composable을 추가합니다.
                    // 예: AdMobBanner()
                    Text(
                        text = "광고 영역 (80dp)",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { innerPadding ->
        // 탭 내부 화면들을 보여줄 NavHost
        NavHost(
            navController = tabNavController,
            startDestination = NavigationDestinations.HOME_SCREEN_TAB, // 탭의 기본 시작 화면
            modifier = Modifier.padding(innerPadding) // Scaffold의 패딩 적용
        ) {
            composable(NavigationDestinations.HOME_SCREEN_TAB) {
                HomeScreen() // modifier를 전달할 필요가 있다면 HomeScreen에서 받도록 수정
            }
            composable(NavigationDestinations.LOCATION_SCREEN_TAB) {
                LocationScreen()
            }
            composable(NavigationDestinations.SETTING_SCREEN_TAB) {
                SettingScreen()
            }
        }
    }
}
