package com.example.moneymetric.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneymetric.data.local.TransactionEntity
import com.example.moneymetric.data.repository.TransactionRepository
import com.example.moneymetric.data.local.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // 1. Data Transaksi (List) yang selalu update otomatis (Hot Flow)
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Total Pemasukan
    val totalIncome: StateFlow<Double?> = repository.getTotalIncome()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // 3. Total Pengeluaran
    val totalExpense: StateFlow<Double?> = repository.getTotalExpense()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val initialCapitalState: StateFlow<Double> = userPreferences.initialCapital
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setInitialCapital(amount: Double) {
        viewModelScope.launch {
            userPreferences.setInitialCapital(amount)
        }
    }

    // Fungsi untuk Simpan Data (Dipanggil saat tombol Simpan ditekan)
    fun saveTransaction(amount: Double, type: String, category: String, description: String) {
        viewModelScope.launch {
            val newTransaction = TransactionEntity(
                amount = amount,
                type = type,
                category = category,
                description = description,
                dateInMillis = System.currentTimeMillis()
            )
            repository.insertTransaction(newTransaction)
        }
    }

    // Fungsi Hapus Data
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}

// --- PENTING: FACTORY ---
// Karena ViewModel kita butuh 'Repository' di dalam kurung (), kita butuh "Pabrik" (Factory)
// untuk membuatnya. Ini standar di Android jika tidak pakai library Hilt/Koin.
class TransactionViewModelFactory(
    private val repository: TransactionRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Masukkan userPreferences ke dalam ViewModel
            return TransactionViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}