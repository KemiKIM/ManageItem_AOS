package com.seongho.manageitem.features.main

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.viewmodel.FRBVM
import com.seongho.manageitem.viewmodel.LocalItemVM
import com.seongho.manageitem.viewmodel.MainVM
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filterNotNull
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,   // For navigation
    mainVM: MainVM,                 // Hoisted from MainActivity
    localItemVM: LocalItemVM,       // Hoisted from AppEntryNavigation
    frbVM: FRBVM = viewModel()      // Get FRBVM for this screen
) {
    val context = LocalContext.current
    val isAuthenticated by mainVM.isUserAuthenticated.collectAsState()
    val deviceCode by mainVM.storedAuthCode.collectAsState()

    LaunchedEffect(isAuthenticated, deviceCode) {
        // 앱 실행 후 최초 1회, 인증이 완료된 시점에 실행
        if (isAuthenticated && deviceCode != null && mainVM.needsAuthCheck()) {
            mainVM.markAuthCheckPerformed()

            // --- 1. 인증 코드 유효성 검사 ---
            frbVM.fetchAuthCode()
            val latestCode = frbVM.authCode.filterNotNull().first()

            Log.d("AuthCheck", "Latest Firebase Code: $latestCode")
            Log.d("AuthCheck", "Stored Device Code: $deviceCode")

            if (latestCode != deviceCode) {
                mainVM.clearAuthentication()
                return@LaunchedEffect // 동기화 로직을 실행하지 않고 즉시 종료
            }

            // --- 2. 데이터 동기화 로직 ---
            Log.d("DataSync", "Starting data sync...")
            // Firebase에서 최신 아이템 목록 가져오기
            frbVM.fetchAllItems()

            // 데이터 로드가 완료될 때까지 기다림 (null이 아닌 리스트가 올 때까지)
            val firebaseItems = frbVM.allItems.filterNotNull().first()
            Log.d("DataSync", "Fetched ${firebaseItems.size} items from Firebase.")

            // Room DB의 모든 데이터를 Firebase 데이터로 교체
            localItemVM.replaceAllItems(firebaseItems)
            Log.d("DataSync", "Replaced local DB with Firebase data.")
        }
    }

    // --- Original UI ---
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("물품 검색") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "검색 아이콘")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "지우기")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        focusManager.clearFocus()
                        val encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.name())
                        navController.navigate("${NavigationDestinations.SEARCHER_SCREEN}?initialQuery=$encodedQuery")
                    }
                }
            )
        )
    }
}
