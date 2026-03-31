package com.example.smartshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartshopping.data.local.entity.PurchaseEntity
import com.example.smartshopping.data.local.model.CategorySummary
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseEntity)

    @Delete
    suspend fun deletePurchase(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchases WHERE userId = :userId ORDER BY purchaseDate DESC, id DESC")
    fun getAllPurchases(userId: Int): Flow<List<PurchaseEntity>>

    @Query("SELECT DISTINCT category FROM purchases WHERE userId = :userId ORDER BY category ASC")
    fun getAllCategories(userId: Int): Flow<List<String>>

    @Query(
        """
        SELECT category,
               SUM(price * quantity) AS totalAmount,
               COUNT(*) AS purchaseCount
        FROM purchases
        WHERE userId = :userId
        GROUP BY category
        ORDER BY totalAmount DESC
        """
    )
    fun getCategorySummary(userId: Int): Flow<List<CategorySummary>>
}
