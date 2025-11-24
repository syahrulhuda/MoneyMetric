package com.example.moneymetric.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneymetric.data.local.AppDatabase
import com.example.moneymetric.data.repository.TransactionRepository
import com.example.moneymetric.ui.screens.DashboardScreen
import com.example.moneymetric.ui.screens.InputTransactionScreen
import com.example.moneymetric.ui.screens.OnboardingScreen
import com.example.moneymetric.ui.viewmodel.TransactionViewModel
import com.example.moneymetric.ui.viewmodel.TransactionViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // 1. Database & Repository
    val database = AppDatabase.getDatabase(context)
    val repository = remember {
        TransactionRepository(
            database.transactionDao(),
            database.debtDao(),
            database.settingDao()
        )
    }

    // 2. ViewModel
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(repository)
    )

    // 4. Cek Status Modal Awal (untuk menentukan halaman start)
    val initialCapital by viewModel.initialCapitalState.collectAsState()

    // Jika modal 0.0, anggap pengguna baru -> ke Onboarding
    // Jika sudah ada isi -> ke Dashboard
    val startDestination = if (initialCapital == 0.0) "onboarding" else "dashboard"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Halaman 0: ONBOARDING
        composable("onboarding") {
            OnboardingScreen(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Halaman 1: DASHBOARD
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToInput = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Halaman 2: INPUT
        composable("input_transaction/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            com.example.moneymetric.ui.screens.InputTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                initialType = type
            )
        }

        // Halaman 3: INPUT UTANG
        composable("input_debt/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            com.example.moneymetric.ui.screens.InputDebtScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                initialType = type
            )
        }

        // Halaman 4: SETTINGS
        composable("settings") {
            com.example.moneymetric.ui.screens.SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}