package com.seongho.manageitem.features.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.*

import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType

import androidx.navigation.NavController
import com.seongho.manageitem.navigation.NavigationDestinations

import com.seongho.manageitem.ui.theme.*
import com.seongho.manageitem.utils.ToastManager
import com.seongho.manageitem.BuildConfig
import com.seongho.manageitem.viewmodel.SettingsVM


@Composable
fun SettingScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsVM = viewModel()
) {
    val context = LocalContext.current



    // AlertDialog 표시 상태
    var showAuthDialog by remember { mutableStateOf(false) }
    // TextField 입력 값 상태
    var authInputText by remember { mutableStateOf("") }
    var showRestartDialog by remember { mutableStateOf(false) }

    val isAuthenticated by settingsViewModel.isUserAuthenticated.collectAsState()

    // Theme
    val modeSettingExpanded = settingsViewModel.modeSettingExpanded
    // StateFlow를 State로 변환하여 UI에 반영
    val selectedAppTheme by settingsViewModel.selectedAppTheme.collectAsState()




    // --- AlertDialog 호출 ---
    if (showAuthDialog) {
        AuthAlertDialog(
            authInputText = authInputText,
            onAuthInputTextChange = { newValue ->
                authInputText = newValue
            },
            onConfirmClicked = {
                // "인증" 버튼 클릭 시 로직

                // ViewModel을 통해 PIN 검증
                val isPinValid = settingsViewModel.checkAuthPin(authInputText)
                val currentInput = authInputText

                showAuthDialog = false // 다이얼로그 닫기
                authInputText = ""     // 입력값 초기화


                if (isPinValid) {
                    settingsViewModel.setUserAuthenticated()
                    showRestartDialog = true
                } else {
                    ToastManager.showToast(context, "인증에 실패하였습니다.")
                }



            },
            onDismissRequest = {
                // 다이얼로그 닫기 요청 시 (외부 클릭, 뒤로가기, "닫기" 버튼)
                showAuthDialog = false // 다이얼로그 닫기
                authInputText = ""     // 입력값 초기화
            }
        )
    }


    // --- 앱 재시작 안내 AlertDialog 호출 ---
    if (showRestartDialog) {
        AppRestartConfirmDialog(
            onConfirmRestart = {
                showRestartDialog = false // 다이얼로그 상태 닫기

                // 앱 재시작 로직
                val packageManager = context.packageManager
                val intent = packageManager.getLaunchIntentForPackage(context.packageName)
                val componentName = intent!!.component
                val mainIntent = Intent.makeRestartActivityTask(componentName)
                context.startActivity(mainIntent)
                (context as? Activity)?.finishAffinity()
                Runtime.getRuntime().exit(0)
            },
            onDismissRequest = { }
        )
    }


    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {

        // 설정화면
        item {
            Text(
                text = "설정화면",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(
                    start = 16.dp,
                    top = 20.dp,
                    bottom = 20.dp
                )
            )
        }



        // TODO: Section 1 (Version, 문의하기)
        item {
            SettingSection {

                // Version
                SettingInfoItem(
                    icon = Icons.Outlined.Info,
                    title = "version",
                    value = BuildConfig.VERSION_NAME
                )


                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color.copy(alpha = 0.2f)
                )


                // 문의하기
                ClickableSettingItem(
                    icon = Icons.Outlined.ArrowCircleUp,
                    title = "문의하기",
                    onClick = {
                        sendEmail(
                            context,
                            "kimseongho@kakao.com",
                            "자재관리 앱 관련 문의")
                    }
                )
            }
        }



        item { Spacer(modifier = Modifier.height(20.dp)) }


        // TODO: Section 2 (인증화면, 모드 설정)
        item {
            SettingSection {

                // 인증화면
                ClickableSettingItem(
                    icon = Icons.Outlined.LockPerson,
                    title = "인증",
                    onClick = {
                        if (!isAuthenticated) { // 인증되지 않았을 때만 다이얼로그 표시
                            showAuthDialog = true
                        } else {
                            // 이미 인증된 경우, 원한다면 토스트 메시지 등을 표시할 수 있음
                        }
                    },
                    trailingContent = { // 여기에 조건부 아이콘 추가
                        Icon(
                            imageVector = if (isAuthenticated) Icons.Filled.HowToReg else Icons.Filled.Dangerous,
                            contentDescription = if (isAuthenticated) "인증됨" else "인증 안됨",
                            modifier = Modifier.size(24.dp), // 아이콘 크기 조절 가능
                            tint = if (isAuthenticated) MSignature else MaterialTheme.colorScheme.error // (선택) 아이콘 색상 변경
                        )
                    }
                )


                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color.copy(alpha = 0.2f)
                )


                // 모드 설정
                ExpandableSettingItem(
                    icon = Icons.Outlined.Contrast,
                    title = "모드 설정",
                    isExpanded = modeSettingExpanded,
                    onHeaderClick = {
                        settingsViewModel.onModeSettingExpanded(!modeSettingExpanded)
                    }
                ) {
                    // 확장 시 보여줄 내용
                    Column(modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp)
                    ) {
                        AppTheme.entries.forEachIndexed { index, theme ->
                            ThemeOptionItem(
                                themeName = theme.displayName,
                                isSelected = selectedAppTheme == theme,
                                onClick = {
                                    settingsViewModel.onThemeSelected(theme) // 선택된 테마 변경
                                }
                            )

                            // 마지막 항목이 아니면 구분선 추가
                            if (index < AppTheme.entries.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 40.dp), // 아이콘 오른쪽부터 구분선 시작
                                    thickness = DividerDefaults.Thickness,
                                    color = DividerDefaults.color.copy(alpha = 0.2f)
                                )
                            }
                        }

                    }
                }
            }
        }



        item { Spacer(modifier = Modifier.height(20.dp)) }




        // TODO: Section 3 (추가하기)
        item {
            SettingSection {
                ClickableSettingItem(
                    icon = Icons.Outlined.AddCircleOutline,
                    title = "추가하기",
                    onClick = {
                        // 예시: 다른 화면으로 이동
                         navController?.navigate(NavigationDestinations.ADD_ITEM_SCREEN)
                    }
                )
            }

        }
    }
}






// TODO: 이메일 전송
private fun sendEmail(context: Context, to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Log.e("SettingScreen", "No email app found")
            ToastManager.showToast(context, "메일 앱을 찾을 수 없습니다.")
        }
    } catch (e: Exception) {
         Log.e("SettingScreen", "Failed to send email", e)
        ToastManager.showToast(context, "메일을 보낼 수 없습니다.")
    }
}




@Composable
fun ThemeOptionItem(
    themeName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = 40.dp, // 들여쓰기 (아이콘 너비 + 간격 고려)
                top = 12.dp,   // 충분한 터치 영역 확보
                bottom = 12.dp,
                end = 0.dp     // Row 끝까지 확장
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = themeName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f) // 텍스트가 남은 공간을 채우도록
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check, // Material 아이콘 사용
                contentDescription = "$themeName 선택됨",
                tint = MaterialTheme.colorScheme.primary, // 테마의 primary 색상 사용
                modifier = Modifier.size(20.dp)
            )
        } else {
            // 선택되지 않았을 때 체크 아이콘 공간을 비워둬서 정렬 유지 (선택사항)
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}


// 설정 항목 그룹을 위한 Composable (iOS 섹션 스타일 유사)
@Composable
fun SettingSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp)) // 모서리 둥글게
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) // 약간의 배경색
    ) {
        content()
    }
}





// 정보 표시용 설정 아이템 (예: 버전)
@Composable
fun SettingInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// 클릭 가능한 설정 아이템 (오른쪽에 > 화살표)
@Composable
fun ClickableSettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(1f)
        )

        //
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = "Navigate", // 또는 null
                modifier = Modifier.size(18.dp)
            )
        }
    }
}




// 확장 가능한 설정 아이템 (예: 모드 설정)
@Composable
fun ExpandableSettingItem(
    icon: ImageVector,
    title: String,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    expandedContent: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onHeaderClick)
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )


            val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f, label = "chevronRotation")
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos, // 화살표 아이콘
                contentDescription = if (isExpanded) "접기" else "펼치기",
                modifier = Modifier
                    .size(18.dp)
                    .rotate(rotationAngle)
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column { // 확장된 내용을 Column으로 감싸서 ColumnScope를 제공
                expandedContent()
            }
        }
    }
}



@Composable
fun AuthAlertDialog(
    authInputText: String,
    onAuthInputTextChange: (String) -> Unit,
    onConfirmClicked: () -> Unit,
    onDismissRequest: () -> Unit // 다이얼로그 외부 클릭 또는 뒤로가기, "닫기" 버튼 공통 처리
) {
    AlertDialog(
        onDismissRequest = onDismissRequest, // 다이얼로그가 닫히도록 요청될 때 호출
        title = { Text("인증") },
        text = {
            Column {
                Text("아래에 입력하시오")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = authInputText,
                    onValueChange = { newValue ->
                        // 숫자만 입력받도록 필터링
                        if (newValue.all { it.isDigit() }) {
                            onAuthInputTextChange(newValue)
                        }
                    },
                    label = { Text("인증번호") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        // "인증" 버튼 클릭 시 호출
        confirmButton = {
            TextButton(
                onClick = onConfirmClicked
            ) {
                Text("인증")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("닫기")
            }
        }
    )
}


@Composable
fun AppRestartConfirmDialog(
    onConfirmRestart: () -> Unit, // "확인" 버튼 클릭 시 실행될 액션
    onDismissRequest: () -> Unit // 다이얼로그 닫기 요청 (선택적 사용)
) {
    AlertDialog(
        onDismissRequest = onDismissRequest, // 외부 클릭이나 뒤로가기로 닫힐 때
        title = { Text("인증 성공") },
        text = { Text("인증에 성공하였습니다. 앱을 다시 시작합니다.") },
        confirmButton = {
            TextButton(
                onClick = onConfirmRestart // 확인 버튼 클릭 시 앱 재시작 로직 실행
            ) {
                Text("확인")}
        }
        // 이 다이얼로그는 보통 '닫기' 버튼이 별도로 필요 없습니다.
        // onDismissRequest를 통해 외부 클릭으로 닫거나, 확인 버튼으로만 닫히게 할 수 있습니다.
    )
}
