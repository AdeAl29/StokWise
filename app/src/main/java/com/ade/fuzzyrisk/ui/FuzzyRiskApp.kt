package com.ade.fuzzyrisk.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ade.fuzzyrisk.ui.screens.AboutScreen
import com.ade.fuzzyrisk.ui.screens.CategoryScreen
import com.ade.fuzzyrisk.ui.screens.DashboardScreen
import com.ade.fuzzyrisk.ui.screens.DataScreen
import com.ade.fuzzyrisk.ui.screens.DetailScreen
import com.ade.fuzzyrisk.ui.screens.HelpScreen
import com.ade.fuzzyrisk.ui.screens.InfoHubScreen
import com.ade.fuzzyrisk.ui.screens.InputScreen
import com.ade.fuzzyrisk.ui.screens.LoginScreen
import com.ade.fuzzyrisk.ui.screens.MainScaffold
import com.ade.fuzzyrisk.ui.screens.RecommendationScreen
import com.ade.fuzzyrisk.ui.screens.RegisterScreen
import com.ade.fuzzyrisk.ui.screens.SplashScreen
import com.ade.fuzzyrisk.ui.screens.StatisticScreen

@Composable
fun FuzzyRiskApp(
    loggedIn: Boolean,
    darkTheme: Boolean,
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit,
    onThemeChange: (Boolean) -> Unit,
    viewModel: FuzzyRiskViewModel = viewModel()
) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()

    fun openApp() {
        onLoginSuccess()
        navController.navigate("dashboard") {
            popUpTo("login") { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = if (loggedIn) "dashboard" else "login") {
        composable("login") {
            LoginScreen(
                onLogin = ::openApp,
                onForgotPassword = ::openApp,
                onRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegister = ::openApp,
                onLogin = { navController.popBackStack() }
            )
        }
        composable("splash") {
            SplashScreen(onDone = ::openApp)
        }
        composable("dashboard") {
            MainScaffold(navController = navController, currentRoute = "dashboard") {
                DashboardScreen(
                    records = state.records,
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange,
                    onInput = { navController.navigate("input") },
                    onDetail = { navController.navigate("detail/$it") },
                    onSeeAll = { navController.navigate("data") }
                )
            }
        }
        composable("input") {
            InputScreen(
                onBack = { navController.popBackStack() },
                onCalculate = viewModel::calculate,
                onSave = viewModel::save
            )
        }
        composable("data") {
            MainScaffold(navController = navController, currentRoute = "data") {
                DataScreen(state.records) { navController.navigate("detail/$it") }
            }
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("id") ?: 0L
            DetailScreen(
                record = state.records.firstOrNull { it.id == id },
                onBack = { navController.popBackStack() }
            )
        }
        composable("stats") {
            MainScaffold(navController = navController, currentRoute = "stats") {
                StatisticScreen(state.records)
            }
        }
        composable("info") {
            MainScaffold(navController = navController, currentRoute = "info") {
                InfoHubScreen(
                    navController = navController,
                    latest = state.records.firstOrNull(),
                    darkTheme = darkTheme,
                    onLogout = {
                        onLogout()
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onThemeChange = onThemeChange
                )
            }
        }
        composable("category") {
            CategoryScreen(records = state.records, onBack = { navController.popBackStack() })
        }
        composable("recommendation") {
            RecommendationScreen(
                latest = state.records.firstOrNull(),
                onBack = { navController.popBackStack() }
            )
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable("help") {
            HelpScreen(onBack = { navController.popBackStack() })
        }
    }
}
