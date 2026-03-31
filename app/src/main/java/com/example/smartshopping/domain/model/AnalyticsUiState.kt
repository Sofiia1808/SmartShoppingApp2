package com.example.smartshopping.domain.model

data class AnalyticsUiState(
    val totalSpent: Double = 0.0,
    val totalPurchases: Int = 0,
    val avgCheck: Double = 0.0,
    val mostPopularCategory: String = "—",
    val uniqueProducts: Int = 0
)
