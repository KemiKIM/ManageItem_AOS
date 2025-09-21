package com.seongho.manageitem.features.entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.viewmodel.AuthVM // ViewModel 사용 시 주석 해제

@Composable
fun AuthScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
//    authViewModel: AuthVM = viewModel() // ViewModel 사용 시 주석 해제
) {
    var numericInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = numericInput,
            onValueChange = { newValue ->
                // 숫자만 입력되도록 필터링
                if (newValue.all { it.isDigit() }) {
                    numericInput = newValue
                }
            },
            label = { Text("숫자 입력") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f) // 화면 너비의 80%
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // HomeScreen으로 이동하고 AuthScreen은 백스택에서 제거
                navController.navigate(NavigationDestinations.MAIN_TABS_SCREEN) {
                    popUpTo(NavigationDestinations.AUTH_SCREEN) {
                        inclusive = true // AuthScreen을 백스택에서 포함하여 제거
                    }
                    launchSingleTop = true // HomeScreen이 이미 스택에 있다면 새로 만들지 않고 재사용
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("홈 화면으로 이동")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    // Preview에서는 실제 NavController가 필요 없으므로, 간단한 rememberNavController() 사용
    // 또는 Preview용 가짜 NavController를 만들 수 있습니다.
    AuthScreen(navController = rememberNavController())
}
