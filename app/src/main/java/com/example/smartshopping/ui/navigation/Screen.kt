package com.example.smartshopping.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Welcome : Screen("welcome", "Welcome")
    data object Login : Screen("login", "Login")
    data object Register : Screen("register", "Register")

    data object Home : Screen("home", "Головна")
    data object Analytics : Screen("analytics", "Аналітика")
    data object Add : Screen("add_purchase", "Додати")
    data object ShoppingList : Screen("shopping_list", "Списки")
    data object History : Screen("history", "Історія")
    data object Recommendations : Screen("recommendations", "Поради")
}