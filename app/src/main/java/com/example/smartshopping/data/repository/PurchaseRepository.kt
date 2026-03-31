package com.example.smartshopping.data.repository

import com.example.smartshopping.data.local.dao.PurchaseDao
import com.example.smartshopping.data.local.entity.PurchaseEntity

class PurchaseRepository(
    private val purchaseDao: PurchaseDao
) {
    fun getAllPurchases(userId: Int) = purchaseDao.getAllPurchases(userId)
    fun getAllCategories(userId: Int) = purchaseDao.getAllCategories(userId)
    fun getCategorySummary(userId: Int) = purchaseDao.getCategorySummary(userId)

    suspend fun addPurchase(purchase: PurchaseEntity) = purchaseDao.insertPurchase(purchase)
    suspend fun deletePurchase(purchase: PurchaseEntity) = purchaseDao.deletePurchase(purchase)
}
