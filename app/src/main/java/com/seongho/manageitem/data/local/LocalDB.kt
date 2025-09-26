package com.seongho.manageitem.data.local // 1단계에서 정한 패키지 경로

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.seongho.manageitem.data.local.dao.ItemDao
import com.seongho.manageitem.data.local.entity.ItemEntity

// UUID를 Room에 저장하고 읽기 위한 TypeConverter (Room 2.3.0 미만 버전에서 필요, 또는 명시적 관리를 원할 때)
// Room 2.3.0 이상에서는 UUID를 기본적으로 지원하지만, 명시적으로 Converter를 만들 수도 있습니다.
// 여기서는 Room 2.3.0+를 가정하고 UUID에 대한 TypeConverter는 생략하겠습니다.
// 만약 문제가 발생하거나 구 버전의 Room을 사용 중이라면 TypeConverter 추가가 필요합니다.

@Database(
    entities = [ItemEntity::class], // 이 데이터베이스에 포함될 엔티티 목록
    version = 1,                    // 데이터베이스 스키마 버전 (스키마 변경 시 이 숫자를 올려야 함)
    exportSchema = false            // 스키마 내보내기 여부 (프로덕션 앱에서는 true로 하고 버전 관리하는 것이 좋음)
)
// @TypeConverters(UuidConverter::class) // 만약 UUID TypeConverter를 만들었다면 여기에 등록
abstract class LocalDB : RoomDatabase() {

    abstract fun itemDao(): ItemDao // ItemDao 인스턴스를 얻기 위한 추상 메서드

    companion object {
        // @Volatile: 이 변수의 값이 다른 스레드에 즉시 보이도록 보장 (캐시되지 않음)
        @Volatile
        private var INSTANCE: LocalDB? = null

        fun getDatabase(context: Context): LocalDB {
            // INSTANCE가 null이면, synchronized 블록 내에서 인스턴스를 생성합니다.
            // 이렇게 하면 여러 스레드에서 동시에 getDatabase를 호출해도 인스턴스가 한 번만 생성됩니다.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // 앱 전체 컨텍스트 사용
                    LocalDB::class.java,    // 생성할 데이터베이스 클래스
                    "manage_item_database"      // 데이터베이스 파일 이름
                )
                    // .fallbackToDestructiveMigration() // (선택적) 마이그레이션 전략: 스키마 버전 변경 시 기존 데이터를 삭제하고 새로 만듦 (개발 중에는 유용)
                    // .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // (선택적) 실제 마이그레이션 경로 설정
                    .build()
                INSTANCE = instance
                // return instance
                instance // 코틀린에서는 마지막 표현식이 반환 값이므로 return 키워드 생략 가능
            }
        }
    }
}