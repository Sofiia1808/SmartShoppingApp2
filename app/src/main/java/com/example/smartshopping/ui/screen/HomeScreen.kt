package com.example.smartshopping.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartshopping.ui.component.GlassInfoCard
import com.example.smartshopping.ui.component.HeroCard
import com.example.smartshopping.ui.component.InsightBanner
import com.example.smartshopping.ui.component.PurchaseRowCard
import com.example.smartshopping.ui.component.SectionTitle
import com.example.smartshopping.util.DateUtils
import com.example.smartshopping.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()
    val purchases by viewModel.purchases.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Привіт, ${user.name.ifBlank { "користувачу" }}!",
                value = String.format("%.0f грн", analytics.totalSpent),
                subtitle = "Загальні витрати за всі покупки. Аналізуй звички та оптимізуй бюджет.",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            SectionTitle("Швидка аналітика")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassInfoCard(
                    title = "Середній чек",
                    value = String.format("%.0f грн", analytics.avgCheck),
                    subtitle = "Середнє значення за покупками",
                    icon = Icons.Filled.Payments,
                    modifier = Modifier.weight(1f)
                )

                GlassInfoCard(
                    title = "Покупки",
                    value = analytics.totalPurchases.toString(),
                    subtitle = "Усього доданих записів",
                    icon = Icons.Filled.ShoppingBag,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            GlassInfoCard(
                title = "Топ категорія",
                value = analytics.mostPopularCategory.ifBlank { "Ще немає даних" },
                subtitle = "Категорія з найбільшою сумою витрат",
                icon = Icons.Filled.LocalOffer,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            InsightBanner(
                text = if (analytics.totalPurchases == 0) {
                    "Поки що список порожній. Додай першу покупку — і одразу з’являться історія, аналітика та поради."
                } else if (recommendations.isNotEmpty()) {
                    "Розумна порада: ${recommendations.first().message}"
                } else {
                    "У тебе ${analytics.uniqueProducts} унікальних товарів. Додавай ще покупки, щоб поради стали точнішими."
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            GlassInfoCard(
                title = "Унікальні товари",
                value = analytics.uniqueProducts.toString(),
                subtitle = "Кількість різних товарів у твоїй базі",
                icon = Icons.Filled.Timeline,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            SectionTitle("Останні покупки", action = if (purchases.size > 5) "Останні" else null)
        }

        if (purchases.isEmpty()) {
            item {
                Text(
                    text = "Тут поки що порожньо. Перейди у вкладку «Додати» та створи перший запис.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(purchases.take(5)) { purchase ->
                PurchaseRowCard(
                    title = purchase.productName,
                    subtitle = "${purchase.category} · ${DateUtils.formatDate(purchase.purchaseDate)}",
                    trailing = String.format("%.0f грн", purchase.price * purchase.quantity),
                    category = purchase.category,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}