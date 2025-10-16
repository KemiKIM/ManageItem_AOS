package com.seongho.manageitem.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.seongho.manageitem.data.local.entity.ItemEntity
import com.seongho.manageitem.navigation.NavigationDestinations // 네비게이션 경로 상수
import com.seongho.manageitem.viewmodel.LocalItemVM
import com.seongho.manageitem.viewmodel.MainVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearcherScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    itemViewModel: LocalItemVM = viewModel(),
    mainVM: MainVM,
    initialQuery: String? = null
) {
    // searchQuery 상태를 rememberSaveable로 변경
    var searchQuery by rememberSaveable { mutableStateOf(initialQuery ?: "") }
    val allItems by itemViewModel.allItems.collectAsState(initial = emptyList())

    var showDetailDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ItemEntity?>(null) }

    // MainVM에서 인증 상태를 구독
    val isAuthenticated by mainVM.isUserAuthenticated.collectAsState()

    val filteredItems = remember(searchQuery, allItems) {
        val results = if (searchQuery.isBlank()) {
            allItems
        } else {
            allItems.filter { item ->
                (item.name?.contains(searchQuery, ignoreCase = true) ?: false) ||
                        (item.partName?.contains(searchQuery, ignoreCase = true) ?: false) ||
                        (item.location?.contains(searchQuery, ignoreCase = true) ?: false) ||
                        (item.serialNumber?.contains(searchQuery, ignoreCase = true) ?: false)
            }
        }
        // 필터링된 결과에 대해 location을 기준으로 오름차순 정렬 적용
        results.sortedBy() { it.location }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
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
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (allItems.isEmpty()) {
                    Text("등록된 물품이 없습니다.")
                } else {
                    Text("검색 결과가 없습니다.")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredItems) { item ->
                    ItemRow(
                        item = item,
                        onItemClick = { clickedItem ->
                            selectedItem = clickedItem
                            showDetailDialog = true
                        }
                    )
                    HorizontalDivider(thickness = DividerDefaults.Thickness, color = DividerDefaults.color)
                }
            }
        }
    }

    if (showDetailDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = {
                showDetailDialog = false
                selectedItem = null
                showDeleteConfirmDialog = false
            },
            title = {
                Text(
                    text = selectedItem?.name ?: "아이템 상세 정보",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("이름: ${selectedItem?.name ?: "-"}")
                    Text("부품명: ${selectedItem?.partName ?: "-"}")
                    Text("위치: ${selectedItem?.location ?: "-"}")
                    Text("S/N: ${selectedItem?.serialNumber ?: "-"}")
                }
            },
            confirmButton = {
//                TextButton(
//                    onClick = {
//                        selectedItem?.id?.let { itemId ->
//                            navController.navigate(NavigationDestinations.ADD_ITEM_SCREEN + "/$itemId")
//                        }
//                        showDetailDialog = false
//                        selectedItem = null
//                        showDeleteConfirmDialog = false
//                    }
//                ) {
//                    Text("편집")
//                }
            },
            dismissButton = {
                if (!isAuthenticated) {
                    TextButton(
                        onClick = {
                            showDeleteConfirmDialog = true
                        }
                    ) {
                        Text("삭제")
                    }
                }
            }
        )
    }

    if (showDeleteConfirmDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog = false
            },
            title = { Text("아이템 삭제 확인") },
            text = { Text("'${selectedItem?.name ?: ""}' 아이템을 정말 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedItem?.let { itemToDelete ->
                            itemViewModel.deleteItem(itemToDelete)
                        }
                        showDeleteConfirmDialog = false
                        showDetailDialog = false
                        selectedItem = null
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
fun ItemRow(
    item: ItemEntity,
    onItemClick: (ItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
            .padding(vertical = 12.dp, horizontal = 8.dp), // 셀의 상하, 좌우 여백 추가
        verticalAlignment = Alignment.CenterVertically // 내용물을 세로 중앙 정렬
    ) {
        // 왼쪽: 위치를 표시하는 박스
        Box(
            modifier = Modifier
                .size(72.dp) // 박스 크기
                .clip(RoundedCornerShape(12.dp)) // 모서리를 둥글게
                .background(MaterialTheme.colorScheme.secondaryContainer), // 배경색
            contentAlignment = Alignment.Center // 내용물을 중앙에 배치
        ) {
            Text(
                text = item.location ?: "-",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        // 박스와 텍스트 사이의 간격
        Spacer(modifier = Modifier.width(16.dp))

        // 오른쪽: 이름, 부품명, S/N을 표시하는 컬럼
        Column(
            modifier = Modifier.weight(1f), // 남은 공간을 모두 차지
            verticalArrangement = Arrangement.spacedBy(4.dp) // 텍스트 간의 세로 간격
        ) {
            // 이름 (굵고 크게)
            Text(
                text = item.name ?: "-",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 부품명
            Text(
                text = item.partName ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // 시리얼 넘버
            Text(
                text = item.serialNumber ?: "-",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
