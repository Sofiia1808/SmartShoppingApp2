package com.example.smartshopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopping.data.local.AppDatabase
import com.example.smartshopping.data.local.entity.PurchaseEntity
import com.example.smartshopping.data.local.entity.ShoppingListItemEntity
import com.example.smartshopping.data.local.model.CategorySummary
import com.example.smartshopping.data.repository.AuthRepository
import com.example.smartshopping.data.repository.PurchaseRepository
import com.example.smartshopping.data.session.UserSessionManager
import com.example.smartshopping.domain.RecommendationEngine
import com.example.smartshopping.domain.model.AnalyticsUiState
import com.example.smartshopping.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val purchaseRepository: PurchaseRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager,
    private val database: AppDatabase
) : ViewModel() {

    private val shoppingListDao = database.shoppingListDao()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    private val _authSuccess = MutableStateFlow<String?>(null)
    val authSuccess: StateFlow<String?> = _authSuccess

    val currentUserId: StateFlow<Int> = sessionManager.currentUserId

    val isLoggedIn = currentUserId
        .map { it > 0 }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            currentUserId.value > 0
        )

    val currentUser = currentUserId
        .flatMapLatest { userId ->
            if (userId <= 0) {
                flowOf(UserProfile())
            } else {
                flowOf(
                    authRepository.getUserById(userId)?.let {
                        UserProfile(it.id, it.name, it.email)
                    } ?: UserProfile()
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val purchases = currentUserId
        .flatMapLatest { userId ->
            if (userId <= 0) flowOf(emptyList())
            else purchaseRepository.getAllPurchases(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = currentUserId
        .flatMapLatest { userId ->
            if (userId <= 0) flowOf(emptyList())
            else purchaseRepository.getAllCategories(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categorySummary = currentUserId
        .flatMapLatest { userId ->
            if (userId <= 0) flowOf(emptyList())
            else purchaseRepository.getCategorySummary(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPurchases = combine(purchases, selectedCategory) { items, selected ->
        if (selected.isNullOrBlank()) items else items.filter { it.category == selected }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analytics = purchases
        .combine(categorySummary) { purchaseList, summary ->
            createAnalyticsState(purchaseList, summary)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsUiState())

    val recommendations = purchases
        .combine(selectedCategory) { items, selected ->
            val source = if (selected.isNullOrBlank()) items else items.filter { it.category == selected }
            RecommendationEngine.generateRecommendations(source)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingListItems = currentUserId
        .flatMapLatest { userId ->
            if (userId <= 0) flowOf(emptyList())
            else shoppingListDao.getItemsForUser(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authError.value = "Заповни всі поля"
            return
        }
        if (password.length < 4) {
            _authError.value = "Пароль має містити щонайменше 4 символи"
            return
        }
        if (password != confirmPassword) {
            _authError.value = "Паролі не співпадають"
            return
        }

        viewModelScope.launch {
            authRepository.register(name, email, password)
                .onSuccess { user ->
                    sessionManager.setLoggedInUser(user.id)
                    _authError.value = null
                    _authSuccess.value = "Акаунт створено успішно"
                }
                .onFailure { error ->
                    _authError.value = error.message ?: "Не вдалося створити акаунт"
                }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authError.value = "Введи email та пароль"
            return
        }

        viewModelScope.launch {
            authRepository.login(email, password)
                .onSuccess { user ->
                    sessionManager.setLoggedInUser(user.id)
                    _authError.value = null
                    _authSuccess.value = "Вхід виконано"
                }
                .onFailure { error ->
                    _authError.value = error.message ?: "Не вдалося увійти"
                }
        }
    }

    fun logout() {
        sessionManager.logout()
        _selectedCategory.value = null
        _authSuccess.value = "Ви вийшли з акаунта"
    }

    fun clearAuthMessages() {
        _authError.value = null
        _authSuccess.value = null
    }

    fun addPurchase(
        productName: String,
        category: String,
        price: Double,
        quantity: Int,
        purchaseDate: Long,
        store: String?,
        note: String?
    ) {
        val userId = currentUserId.value
        if (userId <= 0) return
        if (productName.isBlank() || category.isBlank() || price <= 0.0 || quantity <= 0) return

        viewModelScope.launch {
            purchaseRepository.addPurchase(
                PurchaseEntity(
                    userId = userId,
                    productName = productName.trim(),
                    category = category.trim(),
                    price = price,
                    quantity = quantity,
                    purchaseDate = purchaseDate,
                    store = store?.trim()?.takeIf { it.isNotEmpty() },
                    note = note?.trim()?.takeIf { it.isNotEmpty() }
                )
            )
        }
    }

    fun deletePurchase(item: PurchaseEntity) {
        viewModelScope.launch {
            purchaseRepository.deletePurchase(item)
        }
    }

    fun addShoppingListItem(
        title: String,
        category: String,
        quantity: String = ""
    ) {
        val userId = currentUserId.value
        if (userId <= 0 || title.isBlank()) return

        viewModelScope.launch {
            shoppingListDao.insert(
                ShoppingListItemEntity(
                    userId = userId,
                    title = title.trim(),
                    category = category.trim().ifBlank { "Інше" },
                    quantity = quantity.trim()
                )
            )
        }
    }

    fun toggleShoppingListItem(item: ShoppingListItemEntity) {
        viewModelScope.launch {
            shoppingListDao.update(item.copy(isChecked = !item.isChecked))
        }
    }

    fun deleteShoppingListItem(item: ShoppingListItemEntity) {
        viewModelScope.launch {
            shoppingListDao.delete(item)
        }
    }

    fun deleteCheckedShoppingListItems() {
        val userId = currentUserId.value
        if (userId <= 0) return

        viewModelScope.launch {
            shoppingListDao.deleteChecked(userId)
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    private fun createAnalyticsState(
        purchases: List<PurchaseEntity>,
        summary: List<CategorySummary>
    ): AnalyticsUiState {
        val totalSpent = purchases.sumOf { it.price * it.quantity }
        val totalPurchases = purchases.size
        val avgCheck = if (totalPurchases == 0) 0.0 else totalSpent / totalPurchases
        val mostPopularCategory = summary.maxByOrNull { it.totalAmount }?.category ?: "—"
        val uniqueProducts = purchases.map { it.productName.trim().lowercase() }.distinct().size

        return AnalyticsUiState(
            totalSpent = totalSpent,
            totalPurchases = totalPurchases,
            avgCheck = avgCheck,
            mostPopularCategory = mostPopularCategory,
            uniqueProducts = uniqueProducts
        )
    }
}