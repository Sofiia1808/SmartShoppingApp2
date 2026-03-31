package com.example.smartshopping.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartshopping.data.local.entity.PurchaseEntity
import com.example.smartshopping.ui.component.CategoryChips
import com.example.smartshopping.ui.component.ForecastBanner
import com.example.smartshopping.ui.component.HeroCard
import com.example.smartshopping.ui.component.SectionTitle
import com.example.smartshopping.ui.component.SmartAdviceBanner
import com.example.smartshopping.ui.component.categoryVisual
import com.example.smartshopping.viewmodel.MainViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun RecommendationsScreen(viewModel: MainViewModel) {
    val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()
    val purchases by viewModel.purchases.collectAsStateWithLifecycle()

    val forecast = rememberForecastBannerState(purchases)
    val smartAdviceText = rememberAdviceBannerText(purchases)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Поради та профіль",
                value = user.name.ifBlank { "SmartShopping" },
                subtitle = user.email.ifBlank { "Твій профіль і персональні рекомендації" },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ForecastBanner(
                title = "Прогноз витрат",
                amountText = String.format("%.0f грн", forecast.expectedMonthSpend),
                deltaText = forecast.deltaLabel,
                supportingText = forecast.supportingText,
                positiveDelta = forecast.isOverBudget,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            SmartAdviceBanner(
                title = "Розумна порада",
                message = smartAdviceText,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Мій профіль", style = MaterialTheme.typography.titleLarge)
                    Text("Ім'я: ${user.name}")
                    Text("Email: ${user.email}")
                    Text("Усього покупок: ${analytics.totalPurchases}")
                    Text("Унікальних товарів: ${analytics.uniqueProducts}")

                    Button(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("  Вийти з акаунта")
                    }
                }
            }
        }

        item { SectionTitle("Розумні поради") }

        item {
            CategoryChips(
                categories = categories,
                selected = selectedCategory,
                onSelect = viewModel::setCategory
            )
        }

        if (recommendations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Перші поради з’являться одразу",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Додай хоча б одну покупку, і застосунок почне формувати персональні підказки за товарами та категоріями.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recommendations) { item ->
                val visual = categoryVisual(item.category)

                val cardTitle = when {
                    item.productName.isNotBlank() &&
                            item.productName != "Бюджет тижня" &&
                            item.productName != "Контроль бюджету" -> item.productName
                    else -> item.highlight.ifBlank { item.productName }
                }

                val cardDescription = when {
                    item.highlight.contains("Пора купити", ignoreCase = true) ->
                        "Ймовірно, час знову купити ${item.productName}"

                    item.highlight.contains("Повторювана покупка", ignoreCase = true) ->
                        "Ти регулярно купуєш ${item.productName}. Середній інтервал — ${item.averageIntervalDays} дн."

                    item.highlight.contains("Скоро може знадобитися", ignoreCase = true) ->
                        "Ймовірно, час знову купити ${item.productName}"

                    item.averageIntervalDays > 0 ->
                        "Ти регулярно купуєш ${item.productName}. Середній інтервал — ${item.averageIntervalDays} дн."

                    else -> item.message
                }

                val badgeText = when {
                    item.averageIntervalDays > 0 ->
                        "Категорія ${item.category} · інтервал ${item.averageIntervalDays} дн."
                    item.daysSinceLastPurchase > 0 ->
                        "Остання покупка ${item.daysSinceLastPurchase} дн. тому"
                    else ->
                        "Категорія ${item.category}"
                }

                val probabilityText = if (
                    item.productName != "Бюджет тижня" &&
                    item.productName != "Контроль бюджету"
                ) {
                    "Необхідність: ${item.neededPercent}%"
                } else {
                    null
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                visual.chipBg,
                                                Color.White
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cardTitle,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = cardDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(visual.chipBg)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelLarge,
                                color = visual.tint
                            )
                        }

                        if (probabilityText != null) {
                            Text(
                                text = probabilityText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ForecastBannerState(
    val expectedMonthSpend: Double,
    val deltaLabel: String,
    val supportingText: String,
    val isOverBudget: Boolean
)

private fun rememberForecastBannerState(
    purchases: List<PurchaseEntity>
): ForecastBannerState {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val currentMonth = today.monthValue
    val currentYear = today.year
    val thisMonthPurchases = purchases.filter {
        val date = Instant.ofEpochMilli(it.purchaseDate).atZone(zone).toLocalDate()
        date.monthValue == currentMonth && date.year == currentYear
    }
    val thisMonthSpent = thisMonthPurchases.sumOf { it.price * it.quantity }
    val elapsedDays = today.dayOfMonth.coerceAtLeast(1)
    val daysInMonth = today.lengthOfMonth()
    val expected = if (thisMonthSpent > 0) thisMonthSpent / elapsedDays * daysInMonth else 0.0

    val previousMonths = purchases.groupBy {
        val date = Instant.ofEpochMilli(it.purchaseDate).atZone(zone).toLocalDate()
        date.year to date.monthValue
    }
        .toList()
        .sortedByDescending { it.first.first * 100 + it.first.second }
        .filterNot { it.first.first == currentYear && it.first.second == currentMonth }
        .take(3)

    val budget = if (previousMonths.isNotEmpty()) {
        previousMonths.map { (_, items) -> items.sumOf { it.price * it.quantity } }.average()
    } else {
        purchases.sumOf { it.price * it.quantity }.takeIf { purchases.isNotEmpty() } ?: 0.0
    }

    val delta = expected - budget
    val deltaLabel = when {
        expected == 0.0 -> "Немає даних"
        budget == 0.0 -> "Перший місяць"
        delta > 0 -> "+${delta.absoluteValue.toInt()} грн понад бюджет"
        delta < 0 -> "${delta.absoluteValue.toInt()} грн менше бюджету"
        else -> "У межах бюджету"
    }

    val sourceWeeks = purchases
        .filter {
            val date = Instant.ofEpochMilli(it.purchaseDate).atZone(zone).toLocalDate()
            today.toEpochDay() - date.toEpochDay() <= 21
        }
        .groupBy { Instant.ofEpochMilli(it.purchaseDate).atZone(zone).toLocalDate().with(DayOfWeek.MONDAY) }
        .size
        .coerceAtLeast(1)

    return ForecastBannerState(
        expectedMonthSpend = expected,
        deltaLabel = deltaLabel,
        supportingText = if (purchases.isEmpty()) {
            "Додай кілька покупок, щоб отримати прогноз на місяць."
        } else {
            "На основі твоїх витрат за останні $sourceWeeks тижні."
        },
        isOverBudget = delta > 0
    )
}

private fun rememberAdviceBannerText(
    purchases: List<PurchaseEntity>
): String {
    if (purchases.isEmpty()) {
        return "Додай покупки, і застосунок почне підказувати, де ти економиш, що купуєш повторно та які товари скоро можуть знадобитися знову."
    }

    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val thisWeekStart = today.with(DayOfWeek.MONDAY)
    val prevWeekStart = thisWeekStart.minusWeeks(1)

    val thisWeek = purchases.filter {
        val date = Instant.ofEpochMilli(it.purchaseDate).atZone(zone).toLocalDate()
        !date.isBefore(thisWeekStart)
    }.sumOf { it.price * it.quantity }

    val prevWeek = purchases.filter {
        val date = Instant.ofEpochMilli(it.purchaseDate).atZone(zone).toLocalDate()
        !date.isBefore(prevWeekStart) && date.isBefore(thisWeekStart)
    }.sumOf { it.price * it.quantity }

    if (prevWeek > 0.0) {
        val deltaPercent = ((thisWeek - prevWeek) / prevWeek) * 100
        when {
            deltaPercent <= -10 -> {
                return "Ти витрачаєш на ${deltaPercent.absoluteValue.roundToInt()}% менше, ніж минулого тижня — так тримати."
            }
            deltaPercent >= 15 -> {
                return "Цього тижня витрати вищі на ${deltaPercent.roundToInt()}%. Переглянь великі покупки або часті повтори."
            }
        }
    }

    val totalsByCategory = purchases.groupBy { it.category.ifBlank { "Інше" } }
        .mapValues { (_, items) -> items.sumOf { it.price * it.quantity } }
    val totalSpent = totalsByCategory.values.sum().coerceAtLeast(1.0)
    val topCategory = totalsByCategory.maxByOrNull { it.value }
    if (topCategory != null) {
        val share = topCategory.value / totalSpent * 100
        if (share >= 35) {
            return "Найбільша частка бюджету зараз у категорії «${topCategory.key}» — ${share.roundToInt()}% від усіх витрат."
        }
    }

    val repeated = purchases.groupBy { it.productName.lowercase() }
        .values
        .firstOrNull { it.size >= 3 }
    if (repeated != null) {
        val last = repeated.maxByOrNull { it.purchaseDate }!!
        return "«${last.productName}» уже стала повторюваною покупкою. Її варто тримати у списку потрібних товарів."
    }

    return "Покупки вже формують корисну статистику. Ще трохи історії — і поради стануть точнішими щодо бюджету та повторних покупок."
}