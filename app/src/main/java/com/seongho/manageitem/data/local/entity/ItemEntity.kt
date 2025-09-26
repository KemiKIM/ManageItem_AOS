package com.seongho.manageitem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "items") // Table Name
data class ItemEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(), // Firebase와 공유할 ID

    val location: String?,
    val name: String?,
    val partName: String?,
    val serialNumber: String?,

    // 데이터 변경 추적을 위해 유용한 필드
    val lastUpdated: Long = System.currentTimeMillis()
)