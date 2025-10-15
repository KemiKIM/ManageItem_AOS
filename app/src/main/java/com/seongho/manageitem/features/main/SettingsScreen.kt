package com.seongho.manageitem.features.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seongho.manageitem.BuildConfig
import com.seongho.manageitem.navigation.NavigationDestinations
import com.seongho.manageitem.ui.theme.*
import com.seongho.manageitem.utils.ToastManager
import com.seongho.manageitem.viewmodel.FRBVM
import com.seongho.manageitem.viewmodel.MainVM
import com.seongho.manageitem.viewmodel.SettingsVM
import androidx.core.net.toUri

@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsVM, // Hoisted from MainTabsScreen
    mainVM: MainVM,              // Hoisted from MainTabsScreen
    frbVM: FRBVM = viewModel()   // Used only in this screen
) {
    val context = LocalContext.current

    // AlertDialog 표시 상태
    var showAuthDialog by remember { mutableStateOf(false) }
    // TextField 입력 값 상태
    var authInputText by remember { mutableStateOf("") }
    var showRestartDialog by remember { mutableStateOf(false) }

    // MainVM에서 인증 상태를 구독
    val isAuthenticated by mainVM.isUserAuthenticated.collectAsState()
    val frbAuthCode by frbVM.authCode.collectAsState()

    // Theme (from SettingsVM)
    val modeSettingExpanded = settingsViewModel.modeSettingExpanded
    val selectedAppTheme by settingsViewModel.selectedAppTheme.collectAsState()

    // --- AlertDialog 호출 ---
    if (showAuthDialog) {
        AuthAlertDialog(
            authInputText = authInputText,
            onAuthInputTextChange = { authInputText = it },
            onConfirmClicked = {
                val isPinValid = (frbAuthCode == authInputText)

                showAuthDialog = false // 다이얼로그 닫기

                if (isPinValid && frbAuthCode != null) {
                    // 인증 상태와 코드를 모두 저장
                    mainVM.saveAuthentication(frbAuthCode!!)

                    showRestartDialog = true
                    ToastManager.showToast(context, "인증에 성공하였습니다.")
                } else {
                    ToastManager.showToast(context, "인증에 실패하였습니다.")
                }

                authInputText = "" // 입력값 초기화
            },
            onDismissRequest = {
                showAuthDialog = false
                authInputText = ""
            }
        )
    }

    // --- 앱 재시작 안내 AlertDialog 호출 ---
    if (showRestartDialog) {
        AppRestartConfirmDialog(
            onConfirmRestart = {
                showRestartDialog = false
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
        item {
            Text(
                text = "설정화면",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 20.dp)
            )
        }

        // Section 1 (Version, 문의하기)
        item {
            SettingSection {
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
                ClickableSettingItem(
                    icon = Icons.Outlined.AlternateEmail,
                    title = "문의하기",
                    onClick = {
                        sendEmail(
                            context,
                            "kimseongho@kakao.com",
                            "자재관리 앱 관련 문의"
                        )
                    }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Section 2 (인증화면, 모드 설정)
        item {
            SettingSection {
                ClickableSettingItem(
                    icon = Icons.Outlined.LockPerson,
                    title = "인증",
                    onClick = {
                        if (!isAuthenticated) {
                            frbVM.fetchAuthCode()
                            showAuthDialog = true
                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = if (isAuthenticated) Icons.Filled.HowToReg else Icons.Filled.Dangerous,
                            contentDescription = if (isAuthenticated) "인증됨" else "인증 안됨",
                            modifier = Modifier.size(24.dp),
                            tint = if (isAuthenticated) MSignature else MaterialTheme.colorScheme.error
                        )
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color.copy(alpha = 0.2f)
                )

                ExpandableSettingItem(
                    icon = Icons.Outlined.Contrast,
                    title = "모드 설정",
                    isExpanded = modeSettingExpanded,
                    onHeaderClick = { settingsViewModel.onModeSettingExpanded(!modeSettingExpanded) }
                ) {
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    ) {
                        AppTheme.entries.forEachIndexed { index, theme ->
                            ThemeOptionItem(
                                themeName = theme.displayName,
                                isSelected = selectedAppTheme == theme,
                                onClick = { settingsViewModel.onThemeSelected(theme) }
                            )

                            if (index < AppTheme.entries.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 40.dp),
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


        if (!isAuthenticated) {
            // Section 3 (추가하기) - 미인증사용자
            item {
                SettingSection {
                    ClickableSettingItem(
                        icon = Icons.Outlined.AddCircleOutline,
                        title = "추가하기",
                        onClick = { navController.navigate(NavigationDestinations.ADD_ITEM_SCREEN) }
                    )
                }
            }
        }
    }
}

private fun sendEmail(context: Context, to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Log.e("SettingsScreen", "No email app found")
            ToastManager.showToast(context, "메일 앱을 찾을 수 없습니다.")
        }
    } catch (e: Exception) {
        Log.e("SettingsScreen", "Failed to send email", e)
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
            .padding(start = 40.dp, top = 12.dp, bottom = 12.dp, end = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = themeName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "$themeName 선택됨",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Composable
fun SettingSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        content()
    }
}

@Composable
fun SettingInfoItem(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ClickableSettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ExpandableSettingItem(
    icon: ImageVector,
    title: String,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onHeaderClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = if (isExpanded) "축소" else "확장",
                modifier = Modifier
                    .size(16.dp)
                    .rotate(if (isExpanded) 90f else 0f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        if (isExpanded) {
            content(this)
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
                Text("확인")
            }

        }
        // 이 다이얼로그는 보통 '닫기' 버튼이 별도로 필요 없습니다.
        // onDismissRequest를 통해 외부 클릭으로 닫거나, 확인 버튼으로만 닫히게 할 수 있습니다.
    )
}
