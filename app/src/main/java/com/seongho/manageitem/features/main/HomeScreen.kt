package com.seongho.manageitem.features.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import androidx.navigation.NavController
import com.seongho.manageitem.utils.ToastManager
import com.seongho.manageitem.navigation.NavigationDestinations


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController // 1. NavController 파라미터 추가
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current // 키보드 포커스 관리를 위해 추가

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth(),
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
            // 2. KeyboardOptions 및 KeyboardActions 추가
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        focusManager.clearFocus() // 검색 실행 전 키보드 내리기 (사용자 경험 개선)
                        val encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.name())
                        // NavigationDestinations.SEARCHER_SCREEN 에 라우트 정의가 되어있다고 가정
                        // 예: "searcher_screen?initialQuery={initialQuery}"
                        navController.navigate("${NavigationDestinations.SEARCHER_SCREEN}?initialQuery=$encodedQuery")
                    }
                }
            )
        )
    }
}
