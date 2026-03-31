package com.example.smartshopping.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartshopping.ui.component.SectionTitle

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AuthHeaderCard(
                title = "Вітаємо у SmartShopping",
                subtitle = "Додавай покупки, дивись аналітику та отримуй розумні поради у власному кабінеті."
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard(Icons.Filled.ShoppingCart, "Зручне додавання товарів", "Після входу список буде порожній — ти додаєш лише свої покупки.")
                FeatureCard(Icons.AutoMirrored.Filled.ShowChart, "Аналітика витрат", "Середній чек, категорії та загальна сума рахуються автоматично.")
                FeatureCard(Icons.Filled.Lightbulb, "Розумні поради", "Застосунок підказує повторні покупки на основі твоєї історії.")
            }
        }

        item {
            Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth().height(54.dp)) {
                Text("Увійти")
            }
        }

        item {
            OutlinedButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth().height(54.dp)) {
                Text("Зареєструватися")
            }
        }
    }
}

@Composable
private fun FeatureCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, text: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            SectionTitle(title)
            Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
