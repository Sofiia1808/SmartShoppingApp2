package com.example.smartshopping.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartshopping.viewmodel.MainViewModel

@Composable
fun RegisterScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val error by viewModel.authError.collectAsStateWithLifecycle()
    val success by viewModel.authSuccess.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) viewModel.clearAuthMessages()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AuthHeaderCard(
                title = "Реєстрація",
                subtitle = "Створи акаунт і зберігай власну історію покупок окремо від інших користувачів."
            )
        }

        item {
            Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Ім'я") }, singleLine = true)
                    OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Email") }, singleLine = true)
                    OutlinedTextField(value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Пароль") }, singleLine = true)
                    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Повторіть пароль") }, singleLine = true)
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    success?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    Button(onClick = { viewModel.register(name, email, password, confirmPassword) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Створити акаунт")
                    }
                    TextButton(onClick = onOpenLogin, modifier = Modifier.fillMaxWidth()) {
                        Text("Уже є акаунт? Увійти")
                    }
                    TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Назад")
                    }
                }
            }
        }
    }
}
