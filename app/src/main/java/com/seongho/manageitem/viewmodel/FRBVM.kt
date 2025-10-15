package com.seongho.manageitem.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.seongho.manageitem.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FRBVM : ViewModel() {

    private val database = Firebase.database

    private val _authCode = MutableStateFlow<String?>(null)
    val authCode: StateFlow<String?> = _authCode

    // 초기값을 null로 변경하여 로딩 상태와 빈 리스트 상태를 구별
    private val _allItems = MutableStateFlow<List<ItemEntity>?>(null)
    val allItems: StateFlow<List<ItemEntity>?> = _allItems

    fun fetchAuthCode() {
        val authCodeRef = database.getReference("check").child("auth_code")
        authCodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _authCode.value = snapshot.getValue(String::class.java)
                Log.d("FRBVM", "auth_code: ${_authCode.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FRBVM", "Failed to read auth_code.", error.toException())
            }
        })
    }

    fun fetchAllItems() {
        val itemsRef = database.getReference("allitems")
        itemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ItemEntity>()
                snapshot.children.forEach { itemSnapshot ->
                    val item = ItemEntity(
                        name = itemSnapshot.child("name").getValue(String::class.java) ?: "",
                        location = itemSnapshot.child("location").getValue(String::class.java) ?: "",
                        partName = itemSnapshot.child("partName").getValue(String::class.java) ?: "",
                        serialNumber = itemSnapshot.child("partNumber").getValue(String::class.java)
                    )
                    items.add(item)
                }
                _allItems.value = items
                Log.d("FRBVM", "Fetched ${items.size} items from Firebase.")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FRBVM", "Failed to read allItems.", error.toException())
            }
        })
    }
}