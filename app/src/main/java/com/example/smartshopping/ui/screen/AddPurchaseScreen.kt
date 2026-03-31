package com.example.smartshopping.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartshopping.ui.component.HeroCard
import com.example.smartshopping.ui.component.SectionTitle
import com.example.smartshopping.ui.component.SoftInfoCard
import com.example.smartshopping.util.DateUtils
import com.example.smartshopping.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseScreen(viewModel: MainViewModel) {
    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var store by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showPicker by remember { mutableStateOf(false) }
    var savedMessage by remember { mutableStateOf<String?>(null) }

    val purchases by viewModel.purchases.collectAsStateWithLifecycle()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    val quickCategories = listOf("Продукти", "Напої", "Побут", "Одяг", "Косметика")

    val parsedPrice = price.replace(',', '.').toDoubleOrNull() ?: 0.0
    val parsedQuantity = quantity.toIntOrNull() ?: 1

    val sameProductCount = purchases.count {
        it.productName.equals(productName.trim(), ignoreCase = true)
    }

    val sameCategoryCount = purchases.count {
        it.category.equals(category.trim(), ignoreCase = true)
    }

    val purchaseHint = when {
        productName.isBlank() ->
            "Вкажи назву товару, щоб зберегти покупку."

        category.isBlank() ->
            "Додай категорію — так аналітика і поради будуть точнішими."

        parsedPrice <= 0.0 ->
            "Вкажи коректну ціну, щоб покупка потрапила в аналітику."

        sameProductCount >= 2 ->
            "Товар \"$productName\" уже купували $sameProductCount рази — система зможе підказувати, коли його варто придбати знову."

        sameProductCount == 1 ->
            "\"$productName\" уже є в історії. Після ще однієї покупки з’явиться точніша персональна порада."

        sameCategoryCount >= 3 ->
            "Категорія \"$category\" у тебе часто повторюється — зверни увагу на суму цієї покупки."

        parsedPrice * parsedQuantity >= 1000 ->
            "Це доволі велика покупка. Додай магазин або коментар, щоб потім було зручніше аналізувати витрати."

        else ->
            "Після збереження покупка одразу з’явиться в історії, аналітиці та порадах."
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Додати покупку",
                value = if (price.isBlank()) "Новий запис" else "$price грн",
                subtitle = "Заповни основні поля — товар одразу збережеться у твою особисту історію.",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { SectionTitle("Основні дані") }

        item {
            Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
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

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quickCategories.forEach { chip ->
                            AssistChip(
                                onClick = { category = chip },
                                label = { Text(chip) },
                                colors = AssistChipDefaults.assistChipColors()
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Ціна") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Кількість") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = store,
                        onValueChange = { store = it },
                        label = { Text("Магазин") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Коментар") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Text(
                        text = "Дата покупки: ${DateUtils.formatDate(selectedDate)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showPicker = true }) {
                            Text("Обрати дату")
                        }

                        Button(
                            onClick = {
                                val finalPrice = price.replace(',', '.').toDoubleOrNull() ?: 0.0
                                val finalQuantity = quantity.toIntOrNull() ?: 1

                                if (
                                    productName.isNotBlank() &&
                                    category.isNotBlank() &&
                                    finalPrice > 0 &&
                                    finalQuantity > 0
                                ) {
                                    viewModel.addPurchase(
                                        productName = productName.trim(),
                                        category = category.trim(),
                                        price = finalPrice,
                                        quantity = finalQuantity,
                                        purchaseDate = selectedDate,
                                        store = store.trim(),
                                        note = note.trim()
                                    )

                                    productName = ""
                                    category = ""
                                    price = ""
                                    quantity = "1"
                                    store = ""
                                    note = ""
                                    selectedDate = System.currentTimeMillis()
                                    savedMessage = "Покупку збережено"
                                } else {
                                    savedMessage = "Заповни назву, категорію, ціну та кількість"
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Зберегти")
                        }
                    }

                    savedMessage?.let {
                        Text(
                            text = it,
                            color = if (it.contains("збережено", true)) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        item {
            SoftInfoCard(
                title = "Порада",
                body = purchaseHint,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Скасувати")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}