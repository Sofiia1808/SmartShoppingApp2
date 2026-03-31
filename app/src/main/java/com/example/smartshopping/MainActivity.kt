package com.example.smartshopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.smartshopping.data.local.AppDatabase
import com.example.smartshopping.data.repository.AuthRepository
import com.example.smartshopping.data.repository.PurchaseRepository
import com.example.smartshopping.data.session.UserSessionManager
import com.example.smartshopping.ui.navigation.SmartShoppingNavHost
import com.example.smartshopping.ui.theme.SmartShoppingTheme
import com.example.smartshopping.viewmodel.MainViewModel
import com.example.smartshopping.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)

        MainViewModelFactory(
            purchaseRepository = PurchaseRepository(database.purchaseDao()),
            authRepository = AuthRepository(database.userDao()),
            sessionManager = UserSessionManager(applicationContext),
            database = database // 🔥 ОЦЕ ДОДАТИ
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartShoppingTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SmartShoppingNavHost(viewModel = viewModel)
                }
            }
        }
    }
}
