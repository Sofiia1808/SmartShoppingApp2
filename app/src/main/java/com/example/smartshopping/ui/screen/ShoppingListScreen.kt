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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartshopping.ui.component.HeroCard
import com.example.smartshopping.ui.component.SmartAdviceBanner
import com.example.smartshopping.ui.component.categoryVisual
import com.example.smartshopping.viewmodel.MainViewModel

@Composable
fun ShoppingListScreen(viewModel: MainViewModel) {
    val items by viewModel.shoppingListItems.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Список покупок",
                value = items.size.toString(),
                subtitle = "Додавай товари та відмічай, що вже куплено.",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            SmartAdviceBanner(
                title = if (items.any { !it.isChecked }) "Списки під контролем" else "Список готовий",
                message = when {
                    items.isEmpty() -> "Додай товари, а банер підкаже, скільки позицій ще треба купити."
                    items.all { it.isChecked } -> "Усі товари зі списку вже відмічені як куплені. Можна очистити список або додати нові позиції."
                    else -> {
                        val remaining = items.count { !it.isChecked }
                        val done = items.count { it.isChecked }
                        "У списку ще $remaining позицій. Уже куплено $done — продовжуй у тому ж темпі."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

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
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Назва товару") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Категорія") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Кількість / примітка") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.addShoppingListItem(
                                    title = title,
                                    category = category.ifBlank { "Інше" },
                                    quantity = quantity
                                )
                                title = ""
                                category = ""
                                quantity = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PostAdd,
                            contentDescription = null
                        )
                        Text("  Додати до списку")
                    }
                }
            }
        }

        if (items.isNotEmpty()) {
            item {
                Button(
                    onClick = { viewModel.deleteCheckedShoppingListItems() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Видалити куплені")
                }
            }
        }

        items(items) { shoppingItem ->
            val visual = categoryVisual(shoppingItem.category)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = shoppingItem.isChecked,
                        onCheckedChange = { viewModel.toggleShoppingListItem(shoppingItem) }
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = shoppingItem.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(visual.chipBg)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = shoppingItem.category,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = visual.tint
                                )
                            }

                            if (shoppingItem.quantity.isNotBlank()) {
                                Text(
                                    text = shoppingItem.quantity,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    IconButton(onClick = { viewModel.deleteShoppingListItem(shoppingItem) }) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Видалити",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
