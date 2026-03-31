package com.example.smartshopping.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_items")
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val category: String,
    val quantity: String = "",
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)