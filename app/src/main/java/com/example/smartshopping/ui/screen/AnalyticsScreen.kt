package com.example.smartshopping.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartshopping.data.local.entity.PurchaseEntity
import com.example.smartshopping.ui.component.ExpenseDonutCard
import com.example.smartshopping.ui.component.ExpenseTrendCard
import com.example.smartshopping.ui.component.GlassInfoCard
import com.example.smartshopping.ui.component.HeroCard
import com.example.smartshopping.ui.component.PurchaseRowCard
import com.example.smartshopping.ui.component.SectionTitle
import com.example.smartshopping.viewmodel.MainViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()
    val summary by viewModel.categorySummary.collectAsStateWithLifecycle()
    val purchases by viewModel.purchases.collectAsStateWithLifecycle()

    val weeklyTrend = buildWeeklyTrend(purchases)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Аналітика витрат",
                value = String.format("%.0f грн", analytics.totalSpent),
                subtitle = "Вивчай структуру витрат, середній чек і найсильніші категорії за сумою витрат.",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassInfoCard(
                    title = "Середній чек",
                    value = String.format("%.0f грн", analytics.avgCheck),
                    subtitle = "Середнє значення покупок",
                    icon = Icons.Filled.Payments,
                    modifier = Modifier.weight(1f)
                )
                GlassInfoCard(
                    title = "Покупки",
                    value = analytics.totalPurchases.toString(),
                    subtitle = "Усього записів",
                    icon = Icons.Filled.ShoppingCart,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassInfoCard(
                    title = "Унікальні товари",
                    value = analytics.uniqueProducts.toString(),
                    subtitle = "Різні позиції",
                    icon = Icons.Filled.AutoGraph,
                    modifier = Modifier.weight(1f)
                )
                GlassInfoCard(
                    title = "Топ категорія",
                    value = analytics.mostPopularCategory.ifBlank { "Немає" },
                    subtitle = "Лідер за сумою витрат",
                    icon = Icons.Filled.Category,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            ExpenseTrendCard(
                points = weeklyTrend.points,
                labels = weeklyTrend.labels,
                modifier = Modifier.fillMaxWidth(),
                title = "Динаміка витрат"
            )
        }

        item {
            SectionTitle("Структура витрат")
        }

        if (analytics.totalSpent > 0 && summary.isNotEmpty()) {
            item {
                ExpenseDonutCard(
                    summary = summary,
                    totalAmount = analytics.totalSpent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            item {
                Text(
                    text = "Щоб побачити структуру витрат, додай хоча б одну покупку.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        item {
            SectionTitle("Категорії витрат")
        }

        if (summary.isEmpty()) {
            item {
                Text(
                    text = "Категорії з’являться після додавання покупок.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(summary) { item ->
                PurchaseRowCard(
                    title = item.category,
                    subtitle = "${item.purchaseCount} покупок у категорії",
                    trailing = String.format("%.0f грн", item.totalAmount),
                    category = item.category,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private data class WeeklyTrendUi(
    val points: List<Float>,
    val labels: List<String>
)

private fun buildWeeklyTrend(purchases: List<PurchaseEntity>): WeeklyTrendUi {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val currentWeekStart = today.with(DayOfWeek.MONDAY)
    val weekStarts = (3 downTo 0).map { currentWeekStart.minusWeeks(it.toLong()) }

    val points = weekStarts.map { weekStart ->
        val weekEnd = weekStart.plusWeeks(1)
        purchases
            .filter {
                val date = Instant.ofEpochMilli(it.purchaseDate).atZone(zone).toLocalDate()
                !date.isBefore(weekStart) && date.isBefore(weekEnd)
            }
            .sumOf { it.price * it.quantity }
            .toFloat()
    }

    val labels = weekStarts.mapIndexed { index, weekStart ->
        when (index) {
            weekStarts.lastIndex -> "Цей тиж." 
            weekStarts.lastIndex - 1 -> "Мин. тиж." 
            else -> "${weekStart.dayOfMonth}.${weekStart.monthValue}"
        }
    }

    return WeeklyTrendUi(
        points = if (points.isEmpty()) listOf(0f, 0f, 0f, 0f) else points,
        labels = if (labels.isEmpty()) listOf("—", "—", "—", "—") else labels
    )
}
