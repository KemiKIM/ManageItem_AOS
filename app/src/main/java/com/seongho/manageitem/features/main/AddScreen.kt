package com.seongho.manageitem.features.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold

import androidx.navigation.NavController

import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.ui.theme.*
import com.seongho.manageitem.viewmodel.LocalItemVM

import androidx.compose.runtime.LaunchedEffect
import com.seongho.manageitem.features.ad.GoogleADManager
import com.seongho.manageitem.utils.ToastManager
import com.seongho.manageitem.utils.NetworkUtils

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

    val context = LocalContext.current
    val activity = LocalContext.current as? Activity

    ToastManager.showToast(context, "광고 시청 후 물품을 추가하실 수 있습니다.")

    LaunchedEffect(Unit) {
        if (activity != null) { // activity가 null이 아닐 때만 광고 로드
            GoogleADManager.loadInterstitialAd(activity.applicationContext)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("새 물품 추가") },
                navigationIcon = { // 네비게이션 아이콘 추가
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                }
                // 만약 TopAppBar의 colors를 여기서 직접 설정한다면,
                // 위 isSurfaceLight 계산 시 해당 색상을 사용해야 합니다.
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("물품 이름") },
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

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (itemName.isNotBlank()) {

                        if (!NetworkUtils.isNetworkAvailable(context)) {
                            ToastManager.showToast(context, "인터넷 연결을 확인해주세요.")
                            return@Button
                        }

                        if (activity != null) {
                            GoogleADManager.showInterstitialAd(
                                activity = activity,
                                onAdDismissed = {

                                    itemViewModel.insertItem(
                                        name = itemName,
                                        location = itemLocation,
                                        partName = itemPartName,
                                        serialNumber = itemSerialNumber.ifBlank { null }
                                    )

                                    ToastManager.showToast(context, "추가 완료.")

                                    navController.navigate(NavigationDestinations.MAIN_TABS_SCREEN) {
                                        popUpTo(NavigationDestinations.MAIN_TABS_SCREEN) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MSignature,
                    contentColor = Color.White
                )
            ) {
                Text("추가하기")
            }
        }
    }
}
