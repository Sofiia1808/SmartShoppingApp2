package com.example.smartshopping.domain

import com.example.smartshopping.data.local.entity.PurchaseEntity
import com.example.smartshopping.domain.model.RecommendationItem
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

object RecommendationEngine {

    private const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L
    private const val ONE_WEEK_MILLIS = 7 * ONE_DAY_MILLIS

    fun generateRecommendations(purchases: List<PurchaseEntity>): List<RecommendationItem> {
        if (purchases.isEmpty()) return emptyList()

        val now = System.currentTimeMillis()
        val normalized = purchases
            .sortedBy { it.purchaseDate }
            .map {
                it.copy(
                    productName = it.productName.trim(),
                    category = it.category.trim().ifBlank { "Інше" }
                )
            }

        val recommendations = mutableListOf<RecommendationItem>()

        val totalSpent = normalized.sumOf { it.price * it.quantity }
        val thisWeekSpend = normalized
            .filter { now - it.purchaseDate < ONE_WEEK_MILLIS }
            .sumOf { it.price * it.quantity }
        val previousWeekSpend = normalized
            .filter { now - it.purchaseDate in ONE_WEEK_MILLIS until (2 * ONE_WEEK_MILLIS) }
            .sumOf { it.price * it.quantity }

        if (previousWeekSpend > 0.0) {
            val delta = ((thisWeekSpend - previousWeekSpend) / previousWeekSpend) * 100
            when {
                delta <= -10 -> recommendations += RecommendationItem(
                    productName = "Бюджет тижня",
                    category = "Бюджет",
                    averageIntervalDays = 0,
                    daysSinceLastPurchase = 0,
                    message = "Цього тижня ти витратила на ${delta.absoluteValue.roundToInt()}% менше, ніж минулого. Це хороший темп економії — продовжуй так само.",
                    highlight = "-${delta.absoluteValue.roundToInt()}% до минулого тижня",
                    priority = 130,
                    neededPercent = 0,
                    probabilityPercent = 0
                )

                delta >= 15 -> recommendations += RecommendationItem(
                    productName = "Контроль бюджету",
                    category = "Бюджет",
                    averageIntervalDays = 0,
                    daysSinceLastPurchase = 0,
                    message = "Витрати за цей тиждень вищі на ${delta.roundToInt()}% порівняно з минулим. Переглянь великі покупки або повтори в одній категорії.",
                    highlight = "+${delta.roundToInt()}% до минулого тижня",
                    priority = 128,
                    neededPercent = 0,
                    probabilityPercent = 0
                )
            }
        }

        val categoryTotals = normalized
            .groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.price * it.quantity } }

        val topCategory = categoryTotals.maxByOrNull { it.value }
        if (topCategory != null && totalSpent > 0.0) {
            val share = (topCategory.value / totalSpent) * 100
            if (share >= 35) {
                recommendations += RecommendationItem(
                    productName = topCategory.key,
                    category = topCategory.key,
                    averageIntervalDays = 0,
                    daysSinceLastPurchase = 0,
                    message = "Категорія «${topCategory.key}» займає ${share.roundToInt()}% твого загального бюджету. Саме тут зараз найбільше простору для економії.",
                    highlight = "${share.roundToInt()}% бюджету",
                    priority = 120,
                    neededPercent = 0,
                    probabilityPercent = 0
                )
            }
        }

        normalized
            .groupBy { it.productName.lowercase() }
            .values
            .mapNotNull { items ->
                val sorted = items.sortedBy { it.purchaseDate }
                val last = sorted.last()

                val daysSinceLastPurchase = ((now - last.purchaseDate) / ONE_DAY_MILLIS)
                    .toInt()
                    .coerceAtLeast(0)

                val totalUnits = sorted.sumOf { it.quantity }
                val avgUnits = (totalUnits.toDouble() / sorted.size).roundToInt().coerceAtLeast(1)

                if (sorted.size >= 2) {
                    val intervals = sorted.zipWithNext { first, second ->
                        ((second.purchaseDate - first.purchaseDate) / ONE_DAY_MILLIS)
                            .toInt()
                            .coerceAtLeast(1)
                    }

                    if (intervals.isEmpty()) return@mapNotNull null

                    val averageInterval = intervals.average().roundToInt().coerceAtLeast(1)

                    val neededPercent = ((daysSinceLastPurchase.toDouble() / averageInterval) * 100)
                        .roundToInt()
                        .coerceIn(0, 200)

                    val probabilityPercent = when {
                        neededPercent >= 140 -> 95
                        neededPercent >= 120 -> 90
                        neededPercent >= 100 -> 85
                        neededPercent >= 80 -> 70
                        neededPercent >= 60 -> 55
                        neededPercent >= 40 -> 35
                        else -> 20
                    }

                    val priorityBoost = probabilityPercent + neededPercent / 10

                    when {
                        neededPercent >= 100 -> RecommendationItem(
                            productName = last.productName,
                            category = last.category,
                            averageIntervalDays = averageInterval,
                            daysSinceLastPurchase = daysSinceLastPurchase,
                            message = "Ймовірно, цей товар уже час купувати. Зазвичай ти купуєш його раз на $averageInterval дн., а минуло вже $daysSinceLastPurchase дн.",
                            highlight = "Пора купити знову",
                            priority = 100 + priorityBoost,
                            neededPercent = neededPercent,
                            probabilityPercent = probabilityPercent
                        )

                        neededPercent in 80..99 -> RecommendationItem(
                            productName = last.productName,
                            category = last.category,
                            averageIntervalDays = averageInterval,
                            daysSinceLastPurchase = daysSinceLastPurchase,
                            message = "Скоро може знадобитися покупка. Середній інтервал — $averageInterval дн., від останньої покупки минуло $daysSinceLastPurchase дн.",
                            highlight = "Скоро може знадобитися",
                            priority = 90 + probabilityPercent,
                            neededPercent = neededPercent,
                            probabilityPercent = probabilityPercent
                        )

                        avgUnits >= 2 && averageInterval <= 21 -> RecommendationItem(
                            productName = last.productName,
                            category = last.category,
                            averageIntervalDays = averageInterval,
                            daysSinceLastPurchase = daysSinceLastPurchase,
                            message = "«${last.productName}» повторюється досить часто: у середньому $avgUnits од. раз на $averageInterval днів. Зручно додавати його у список наперед.",
                            highlight = "Повторювана покупка",
                            priority = 80,
                            neededPercent = neededPercent,
                            probabilityPercent = probabilityPercent
                        )

                        else -> RecommendationItem(
                            productName = last.productName,
                            category = last.category,
                            averageIntervalDays = averageInterval,
                            daysSinceLastPurchase = daysSinceLastPurchase,
                            message = "Поки що купувати рано. Зазвичай ти купуєш цей товар раз на $averageInterval дн.",
                            highlight = "Регулярна покупка",
                            priority = 60,
                            neededPercent = neededPercent,
                            probabilityPercent = probabilityPercent
                        )
                    }
                } else {
                    val amount = (last.price * last.quantity).roundToInt()
                    RecommendationItem(
                        productName = last.productName,
                        category = last.category,
                        averageIntervalDays = 0,
                        daysSinceLastPurchase = daysSinceLastPurchase,
                        message = "Поки що «${last.productName}» купувалась лише один раз на суму $amount грн. Після кількох повторів застосунок зможе точніше передбачати, коли її варто купити знову.",
                        highlight = "Замало історії",
                        priority = 25,
                        neededPercent = 0,
                        probabilityPercent = 20
                    )
                }
            }
            .forEach { recommendations += it }

        return recommendations
            .distinctBy { it.productName to it.highlight }
            .sortedByDescending { it.priority }
            .take(8)
    }
}