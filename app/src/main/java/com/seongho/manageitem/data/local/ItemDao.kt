package com.seongho.manageitem.data.local.dao

import androidx.room.*
import com.seongho.manageitem.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ItemDao {

    /**
     * 새로운 아이템을 데이터베이스에 삽입합니다.
     * 만약 동일한 id의 아이템이 이미 존재한다면, 새로운 아이템으로 대체합니다 (OnConflictStrategy.REPLACE).
     * @param itemEntity 삽입할 아이템 엔티티
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(itemEntity: ItemEntity)

    /**
     * 여러 아이템을 데이터베이스에 삽입합니다.
     * 충돌 발생 시 새로운 아이템으로 대체합니다.
     * @param items 삽입할 아이템 엔티티 목록
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>) // 여러 개 삽입 예시

    /**
     * 기존 아이템 정보를 업데이트합니다.
     * @param itemEntity 업데이트할 아이템 엔티티
     */
    @Update
    suspend fun updateItem(itemEntity: ItemEntity)

    /**
     * 특정 아이템을 데이터베이스에서 삭제합니다.
     * @param itemEntity 삭제할 아이템 엔티티 (주로 id를 기준으로 삭제)
     */
    @Delete
    suspend fun deleteItem(itemEntity: ItemEntity)

    /**
     * ID를 기준으로 특정 아이템 정보를 조회합니다.
     * Flow를 반환하여 데이터 변경 시 자동으로 UI가 업데이트되도록 할 수 있습니다.
     * @param itemId 조회할 아이템의 UUID
     * @return 해당 ID의 아이템 엔티티를 담은 Flow (없으면 null)
     */
    @Query("SELECT * FROM items WHERE id = :itemId")
    fun getItemById(itemId: UUID): Flow<ItemEntity?>

    /**
     * 데이터베이스에 저장된 모든 아이템 목록을 조회합니다.
     * Flow를 반환하여 데이터 변경 시 자동으로 UI가 업데이트되도록 할 수 있습니다.
     * (예: 이름을 기준으로 오름차순 정렬)
     * @return 모든 아이템 엔티티 목록을 담은 Flow
     */
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems(): Flow<List<ItemEntity>>

    /**
     * (선택적) 모든 아이템을 데이터베이스에서 삭제합니다.
     */
    @Query("DELETE FROM items")
    suspend fun deleteAllItems()

    /**
     * 데이터베이스의 모든 아이템을 삭제하고 새로운 목록으로 교체합니다.
     * 이 작업은 하나의 트랜잭션으로 처리되어 데이터 정합성을 보장합니다.* @param items 새로 삽입할 아이템 목록
     */
    @Transaction
    suspend fun replaceAll(items: List<ItemEntity>) {
        deleteAllItems()
        insertItems(items)
    }
}
