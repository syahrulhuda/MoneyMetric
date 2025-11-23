package com.example.moneymetric.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneymetric.data.local.AppDatabase
import com.example.moneymetric.data.repository.TransactionRepository
import com.example.moneymetric.ui.screens.DashboardScreen
import com.example.moneymetric.ui.viewmodel.TransactionViewModel
import com.example.moneymetric.ui.viewmodel.TransactionViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = AppDatabase.getDatabase(context)
    val repository = remember { TransactionRepository(database.transactionDao()) }
    // Tambah ini
    val userPreferences = remember { UserPreferences(context) }

    // Update Factory dengan userPreferences
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(repository, userPreferences)
    )

    // Cek data modal awal
    val initialCapital by viewModel.initialCapitalState.collectAsState()

    // Tentukan halaman awal: Jika null (belum pernah isi), ke Onboarding
    // Catatan: Logika ini sederhana. Idealnya pakai Splash Screen, tapi ini cukup untuk sekarang.
    val startDestination = "onboarding" // Kita paksa cek di dalam NavHost nanti

    NavHost(navController = navController, startDestination = startDestination) {

        // Halaman 0: ONBOARDING
        composable("onboarding") {
            // Jika sudah ada data (tidak null), langsung lempar ke dashboard
            // Logika redirect sederhana
            if (initialCapital != 0.0) { // Asumsi 0.0 adalah default belum diisi/dilewati
                // Bisa ditambahkan logic lebih kompleks, tapi kita manual dulu
            }

            OnboardingScreen(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true } // Hapus onboarding dari backstack
                    }
                }
            )
        }

        // Halaman 1: DASHBOARD
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToInput = { navController.navigate("input_transaction") }
            )
        }

        // Halaman 2: INPUT TRANSAKSI (Baru!)
        composable("input_transaction") {
            // Panggil layar Input yang baru kita buat
            // Kita perlu import: com.example.moneymetric.ui.screens.InputTransactionScreen
            com.example.moneymetric.ui.screens.InputTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    // Aksi saat tombol Back atau Simpan ditekan
                    navController.popBackStack()
                }
            )
        }
    }
}