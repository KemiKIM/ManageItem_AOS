package com.seongho.manageitem.features.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController

import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.viewmodel.LocalItemVM

import com.seongho.manageitem.features.ad.InterstitialAdManager

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavController,
    itemViewModel: LocalItemVM
) {
    var itemName by remember { mutableStateOf("") }
    var itemLocation by remember { mutableStateOf("") }
    var itemPartName by remember { mutableStateOf("") }
    var itemSerialNumber by remember { mutableStateOf("") }

    val activity = LocalContext.current as? Activity

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("새 아이템 추가") })
        },
        bottomBar = {
            Button(
                onClick = {
                    if (itemName.isNotBlank()) {
                        itemViewModel.insertItem(
                            name = itemName,
                            location = itemLocation,
                            partName = itemPartName,
                            serialNumber = itemSerialNumber.ifBlank { null } // 비어있으면 null로
                        )


                        // 전면광고 Open
                        if (activity != null) {
                            InterstitialAdManager.showAd(
                                activity = activity,
                                onAdDismissed = {

                                    // HomeScreen으로 돌아가기 (이전 스택 제거 옵션 고려)
                                    navController.navigate(NavigationDestinations.MAIN_TABS_SCREEN) {
                                        popUpTo(NavigationDestinations.MAIN_TABS_SCREEN) {
                                            inclusive = true // MAIN_TABS_SCREEN 포함 이전 스택 모두 제거
                                        }
                                        launchSingleTop =
                                            true  // MAIN_TABS_SCREEN이 이미 스택에 있으면 재생성하지 않음
                                    }
                                }
                            )
                        }

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, vertical = 8.dp)
            ) {
                Text("추가하기")
            }
        }
    ) {
            paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("아이템 이름 (필수)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = itemLocation,
                onValueChange = { itemLocation = it },
                label = { Text("위치") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = itemPartName,
                onValueChange = { itemPartName = it },
                label = { Text("부품명") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = itemSerialNumber,
                onValueChange = { itemSerialNumber = it },
                label = { Text("시리얼 번호") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }

}
