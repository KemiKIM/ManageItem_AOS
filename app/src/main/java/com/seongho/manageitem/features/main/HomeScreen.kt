package com.seongho.manageitem.features.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import com.seongho.manageitem.data.local.entity.ItemEntity
import com.seongho.manageitem.viewmodel.LocalItemVM

@OptIn(ExperimentalMaterial3Api::class) // Scaffold 사용을 위해
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    itemViewModel: LocalItemVM = viewModel() // Inject ItemViewModel
) {
    // Collect the list of items from the ViewModel
    val items by itemViewModel.allItems.collectAsState(initial = emptyList())

    // Main layout: Column containing a button and the list of items
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Add some padding
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally // Center button, etc.
    ) {

        Spacer(modifier = Modifier.height(16.dp)) // Add space between button and list

        // Check if the list is empty
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("아이템이 없습니다. 추가 버튼을 눌러 새 아이템을 등록하세요.")
            }
        } else {
            // List of items
            LazyColumn(
                modifier = Modifier.fillMaxWidth() // Take available width
            ) {
                items(items) { item ->
                    ItemRow(item = item, onDelete = {
                        itemViewModel.deleteItem(item)
                    })
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    // Add a divider between items
                }
            }
        }
    }
}

// Composable function to display a single item row (can be in this file or a separate one)
@Composable
fun ItemRow(item: ItemEntity, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Add vertical padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) { // Allow text to take up space
            Text(text = "이름: ${item.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "부품명: ${item.partName}", style = MaterialTheme.typography.bodySmall)
            Text(text = "위치: ${item.location}", style = MaterialTheme.typography.bodySmall)
            item.serialNumber?.let { // Display serial number only if it exists
                Text(text = "S/N: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "아이템 삭제")
        }
    }
}