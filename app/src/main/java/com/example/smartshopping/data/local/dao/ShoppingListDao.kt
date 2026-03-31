package com.example.smartshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartshopping.data.local.entity.ShoppingListItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("""
        SELECT * FROM shopping_list_items
        WHERE userId = :userId
        ORDER BY isChecked ASC, createdAt DESC
    """)
    fun getItemsForUser(userId: Int): Flow<List<ShoppingListItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingListItemEntity)

    @Update
    suspend fun update(item: ShoppingListItemEntity)

    @Delete
    suspend fun delete(item: ShoppingListItemEntity)

    @Query("DELETE FROM shopping_list_items WHERE userId = :userId AND isChecked = 1")
    suspend fun deleteChecked(userId: Int)
}