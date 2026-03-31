package com.example.smartshopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartshopping.data.local.AppDatabase
import com.example.smartshopping.data.repository.AuthRepository
import com.example.smartshopping.data.repository.PurchaseRepository
import com.example.smartshopping.data.session.UserSessionManager

class MainViewModelFactory(
    private val purchaseRepository: PurchaseRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager,
    private val database: AppDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                purchaseRepository = purchaseRepository,
                authRepository = authRepository,
                sessionManager = sessionManager,
                database = database
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}