package com.seongho.manageitem.features.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.seongho.manageitem.navigation.NavigationDestinations

import com.seongho.manageitem.R

data class LocationInfo(
    @DrawableRes val icon: Int,
    val title: String
)

@Composable
fun LocationScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null
) {

    val locationItems = listOf(
        LocationInfo(icon = R.drawable.location_alpha_a, title = "A 구역"),
        LocationInfo(icon = R.drawable.location_alpha_b, title = "B 구역"),
        LocationInfo(icon = R.drawable.location_alpha_c, title = "C 구역"),
        LocationInfo(icon = R.drawable.location_alpha_d, title = "D 구역"),
        LocationInfo(icon = R.drawable.location_alpha_e, title = "E 구역"),
        LocationInfo(icon = R.drawable.location_alpha_f, title = "F 구역"),
        LocationInfo(icon = R.drawable.location_alpha_g, title = "G 구역"),
        LocationInfo(icon = R.drawable.location_alpha_c_fill, title = "CABINET")
    )


    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "배치도",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 20.dp)
            )
        }

        item {
            SettingSection {
                // 3. 리스트를 순회하며 아이템과 구분선을 동적으로 생성
                locationItems.forEachIndexed { index, item ->
                    ClickableLocationItem(
                        icon = item.icon,
                        title = item.title,
                        onClick = {
                            navController?.navigate("${NavigationDestinations.LOCATION_VIEW_SCREEN}/${item.title}")
                        }
                    )

                    // 마지막 아이템이 아닐 경우에만 구분선 추가
                    if (index < locationItems.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 56.dp),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun ClickableLocationItem(
    @DrawableRes icon: Int, // @DrawableRes 어노테이션 추가 (권장)
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
            painter = painterResource(id = icon),
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