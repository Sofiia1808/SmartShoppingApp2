package com.example.smartshopping.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartshopping.ui.screen.AddPurchaseScreen
import com.example.smartshopping.ui.screen.AnalyticsScreen
import com.example.smartshopping.ui.screen.HistoryScreen
import com.example.smartshopping.ui.screen.HomeScreen
import com.example.smartshopping.ui.screen.RecommendationsScreen
import com.example.smartshopping.ui.screen.ShoppingListScreen
import com.example.smartshopping.ui.screen.auth.LoginScreen
import com.example.smartshopping.ui.screen.auth.RegisterScreen
import com.example.smartshopping.ui.screen.auth.WelcomeScreen
import com.example.smartshopping.viewmodel.MainViewModel

@Composable
fun SmartShoppingNavHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val bottomItems = listOf(
        Screen.Home,
        Screen.Analytics,
        Screen.Add,
        Screen.ShoppingList,
        Screen.History,
        Screen.Recommendations
    )

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(Screen.Welcome.route) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val showBottomBar = bottomItems.any { it.route == currentBackStackEntry?.destination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { screen ->
                        val selected = currentBackStackEntry
                            ?.destination
                            ?.hierarchy
                            ?.any { it.route == screen.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(),
                            icon = {
                                val icon = when (screen) {
                                    Screen.Home -> Icons.Filled.Home
                                    Screen.Analytics -> Icons.AutoMirrored.Filled.ShowChart
                                    Screen.Add -> Icons.Filled.AddCircle
                                    Screen.ShoppingList -> Icons.Filled.FormatListBulleted
                                    Screen.History -> Icons.Filled.ReceiptLong
                                    Screen.Recommendations -> Icons.Filled.Lightbulb
                                    else -> Icons.Filled.Home
                                }

                                Icon(
                                    imageVector = icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    maxLines = 1,
                                    softWrap = false,
                                    textAlign = TextAlign.Center,
                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                                )
                            },
                            alwaysShowLabel = true
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Welcome.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onLoginClick = { navController.navigate(Screen.Login.route) },
                    onRegisterClick = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(viewModel)
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(viewModel)
            }

            composable(Screen.Add.route) {
                AddPurchaseScreen(viewModel)
            }

            composable(Screen.ShoppingList.route) {
                ShoppingListScreen(viewModel)
            }

            composable(Screen.History.route) {
                HistoryScreen(viewModel)
            }

            composable(Screen.Recommendations.route) {
                RecommendationsScreen(viewModel)
            }
        }
    }
}