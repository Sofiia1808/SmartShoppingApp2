package com.example.smartshopping.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryVisual(
    val icon: ImageVector,
    val tint: Color,
    val chipBg: Color
)

fun categoryVisual(category: String): CategoryVisual {
    return when (category.trim().lowercase()) {
        "продукти" -> CategoryVisual(
            icon = Icons.Filled.Fastfood,
            tint = Color(0xFFFF9F43),
            chipBg = Color(0xFFFFF2E5)
        )
        "напої" -> CategoryVisual(
            icon = Icons.Filled.LocalDrink,
            tint = Color(0xFF20C6BE),
            chipBg = Color(0xFFE8FBF9)
        )
        "кава" -> CategoryVisual(
            icon = Icons.Filled.Coffee,
            tint = Color(0xFF9C6B4F),
            chipBg = Color(0xFFF7EEE8)
        )
        "побут" -> CategoryVisual(
            icon = Icons.Filled.CleaningServices,
            tint = Color(0xFF4D96FF),
            chipBg = Color(0xFFEBF4FF)
        )
        "одяг" -> CategoryVisual(
            icon = Icons.Filled.Checkroom,
            tint = Color(0xFFA56EFF),
            chipBg = Color(0xFFF3ECFF)
        )
        "косметика" -> CategoryVisual(
            icon = Icons.Filled.Spa,
            tint = Color(0xFFFF7EB6),
            chipBg = Color(0xFFFFEEF5)
        )
        else -> CategoryVisual(
            icon = Icons.Filled.ShoppingBag,
            tint = Color(0xFF20C6BE),
            chipBg = Color(0xFFE8FBF9)
        )
    }
}

fun storeVisualTint(store: String?): Color {
    return if (store.isNullOrBlank()) Color(0xFF7E8B91) else Color(0xFF20C6BE)
}

fun storeIcon(): ImageVector = Icons.Filled.Storefront