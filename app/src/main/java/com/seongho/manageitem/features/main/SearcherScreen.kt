package com.seongho.manageitem.features.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.seongho.manageitem.data.local.entity.ItemEntity
import com.seongho.manageitem.navigation.NavigationDestinations // 네비게이션 경로 상수
import com.seongho.manageitem.viewmodel.LocalItemVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearcherScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    itemViewModel: LocalItemVM = viewModel(),
    initialQuery: String? = null
) {
    // searchQuery 상태를 rememberSaveable로 변경
    var searchQuery by rememberSaveable { mutableStateOf(initialQuery ?: "") }
    val allItems by itemViewModel.allItems.collectAsState(initial = emptyList())

    var showDetailDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ItemEntity?>(null) }

    val filteredItems = remember(searchQuery, allItems) {
        if (searchQuery.isBlank()) {
            allItems
        } else {
            allItems.filter { item ->
                (item.name?.contains(searchQuery, ignoreCase = true) ?: false) ||
                (item.partName?.contains(searchQuery, ignoreCase = true) ?: false) ||
                (item.location?.contains(searchQuery, ignoreCase = true) ?: false) ||
                (item.serialNumber?.contains(searchQuery, ignoreCase = true) ?: false)
            }
        }
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
            placeholder = { Text("아이템 검색") },
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
                    Text("아이템이 없습니다. '추가' 탭에서 새 아이템을 등록하세요.")
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
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = true
                    }
                ) {
                    Text("삭제")
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name ?: "-", style = MaterialTheme.typography.titleMedium)
            Text(text = "부품명: ${item.partName ?: "-"}", style = MaterialTheme.typography.bodySmall)
            Text(text = "위치: ${item.location ?: "-"}", style = MaterialTheme.typography.bodySmall)
            item.serialNumber?.let {
                Text(text = "S/N: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
