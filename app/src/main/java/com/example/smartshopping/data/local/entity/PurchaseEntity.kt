package com.example.smartshopping.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val productName: String,
    val category: String,
    val price: Double,
    val quantity: Int,
    val purchaseDate: Long,
    val store: String? = null,
    val note: String? = null
)
