package com.seongho.manageitem.viewmodel // ViewModel 패키지 경로

import android.app.Application
import androidx.lifecycle.*
import com.seongho.manageitem.data.local.LocalDB // LocalDB import
import com.seongho.manageitem.data.local.dao.ItemDao
import com.seongho.manageitem.data.local.entity.ItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

// ViewModel은 Application Context를 알아야 LocalDB 인스턴스를 가져올 수 있으므로 AndroidViewModel 사용
class LocalItemVM(application: Application) : AndroidViewModel(application) {

    // ItemDao 인스턴스를 ViewModel 생성 시 초기화
    private val itemDao: ItemDao

    // 모든 아이템 목록을 Flow 형태로 관찰 가능하도록 노출
    val allItems: Flow<List<ItemEntity>>

    init {
        // LocalDB 인스턴스를 통해 ItemDao를 가져옴
        val database = LocalDB.getDatabase(application)
        itemDao = database.itemDao()

        // Dao로부터 모든 아이템 목록 Flow를 가져옴
        allItems = itemDao.getAllItems()
    }

    /**
     * 새로운 아이템을 데이터베이스에 삽입합니다.
     * ViewModel의 코루틴 스코프(viewModelScope)를 사용하여 백그라운드 스레드에서 실행합니다.
     */
    fun insertItem(name: String, location: String, partName: String, serialNumber: String?) {
        viewModelScope.launch(Dispatchers.IO) { // IO 스레드에서 DB 작업 수행
            val newItem = ItemEntity(
                // id는 ItemEntity에서 기본값으로 UUID.randomUUID()로 생성됨
                name = name,
                location = location,
                partName = partName,
                serialNumber = serialNumber
                // lastUpdated는 ItemEntity에서 기본값으로 System.currentTimeMillis()로 생성됨
            )
            itemDao.insertItem(newItem)
        }
    }

    /**
     * 기존 아이템 정보를 업데이트합니다.
     */
    fun updateItem(itemEntity: ItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.updateItem(itemEntity)
        }
    }

    /**
     * 특정 아이템을 삭제합니다.
     */
    fun deleteItem(itemEntity: ItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.deleteItem(itemEntity)
        }
    }

    /**
     * ID로 특정 아이템을 조회합니다. (결과는 Flow로 반환됨)
     * 이 메서드는 UI에서 직접 Flow를 구독하여 사용할 수 있습니다.
     */
    fun getItemById(itemId: UUID): Flow<ItemEntity?> {
        return itemDao.getItemById(itemId)
    }

    /**
     * (선택적) 모든 아이템을 삭제합니다.
     */
    fun deleteAllItems() {
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.deleteAllItems()
        }
    }
}