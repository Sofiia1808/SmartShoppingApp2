package com.example.smartshopping.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartshopping.data.local.dao.PurchaseDao
import com.example.smartshopping.data.local.dao.ShoppingListDao
import com.example.smartshopping.data.local.dao.UserDao
import com.example.smartshopping.data.local.entity.PurchaseEntity
import com.example.smartshopping.data.local.entity.ShoppingListItemEntity
import com.example.smartshopping.data.local.entity.UserEntity

@Database(
    entities = [
        PurchaseEntity::class,
        UserEntity::class,
        ShoppingListItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun purchaseDao(): PurchaseDao
    abstract fun userDao(): UserDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_shopping_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}