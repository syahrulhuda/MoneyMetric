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
import com.example.moneymetric.data.local.UserPreferences
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
            database.debtDao()
        )
    }

    // 2. UserPreferences (DataStore)
    val userPreferences = remember { UserPreferences(context) }

    // 3. ViewModel (Sekarang pakai 2 parameter!)
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(repository, userPreferences)
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
                    // Hapus history onboarding agar tidak bisa back
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
                onNavigateToInput = {
                    navController.navigate("input_transaction")
                }
            )
        }

        // Halaman 2: INPUT
        composable("input_transaction") {
            InputTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}