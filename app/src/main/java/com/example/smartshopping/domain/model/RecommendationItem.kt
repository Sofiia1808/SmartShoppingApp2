package com.example.smartshopping.domain.model

data class RecommendationItem(
    val productName: String,
    val category: String,
    val averageIntervalDays: Int,
    val daysSinceLastPurchase: Int,
    val message: String,
    val highlight: String,
    val priority: Int,
    val neededPercent: Int = 0,
    val probabilityPercent: Int = 0
)