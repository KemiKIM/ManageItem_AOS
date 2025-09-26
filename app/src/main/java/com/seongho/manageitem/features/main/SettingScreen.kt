package com.seongho.manageitem.features.main

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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.outlined.*

import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController // NavController import 추가
import androidx.navigation.compose.rememberNavController // Preview를 위해 추가
import com.seongho.manageitem.navigation.NavigationDestinations // NavigationDestinations import 추가

import com.seongho.manageitem.ui.theme.*
import com.seongho.manageitem.utils.ToastManager
import com.seongho.manageitem.BuildConfig // 앱 버전을 가져오기 위해 필요
import com.seongho.manageitem.viewmodel.SettingsVM


@Composable
fun SettingScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsVM = viewModel()
) {
    val context = LocalContext.current

    // Auth
    val authScreenEnabled by settingsViewModel.authScreenEnabled.collectAsState()


    // Theme
    val modeSettingExpanded = settingsViewModel.modeSettingExpanded
    // StateFlow를 State로 변환하여 UI에 반영
    val selectedAppTheme by settingsViewModel.selectedAppTheme.collectAsState()


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
                ToggleSettingItem(
                    icon = Icons.Outlined.LockPerson,
                    title = "인증화면",
                    checked = authScreenEnabled,
                    onCheckedChange = { newState ->
                        settingsViewModel.onAuthScreenToggled(newState)
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
    onClick: () -> Unit
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
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            contentDescription = "Navigate",
            modifier = Modifier
                .size(18.dp)
        )
    }
}




// 토글 스위치가 있는 설정 아이템 (기존 코드 활용)
@Composable
fun ToggleSettingItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,  // 이 checked 값은 ViewModel로부터 collectAsState()로 온 값
    onCheckedChange: (Boolean) -> Unit // 이 콜백은 ViewModel의 함수를 호출해야 합니다.
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ), // 토글은 높이가 조금 더 작을 수 있음
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MSignature, // <<< 전역 색상 사용
                checkedBorderColor = MSignature.copy(alpha = 0.7f),

                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
            )
        )
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
