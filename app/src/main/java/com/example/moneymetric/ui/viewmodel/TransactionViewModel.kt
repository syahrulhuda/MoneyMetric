package com.example.moneymetric.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moneymetric.data.local.DebtEntity
import com.example.moneymetric.data.local.TransactionEntity
import com.example.moneymetric.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    // BAGIAN 1: TRANSAKSI (KODE LAMA - TETAP)

    // 1. Data Transaksi (List)
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Total Pemasukan
    val totalIncome: StateFlow<Double> = repository.getTotalIncome()
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // 3. Total Pengeluaran
    val totalExpense: StateFlow<Double> = repository.getTotalExpense()
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // 4. Modal Awal (BARU: dari Repository)
    val initialCapitalState: StateFlow<Double> = repository.getInitialCapital()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setInitialCapital(amount: Double) {
        viewModelScope.launch {
            repository.setInitialCapital(amount)
        }
    }

    // Simpan Transaksi
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

    // Hapus Transaksi
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // BAGIAN 2: UTANG / PIUTANG (KODE BARU)

    // 1. List Semua Utang/Piutang
    val allDebts: StateFlow<List<DebtEntity>> = repository.getAllDebts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Total Utang (Kita berutang ke orang)
    val totalDebt: StateFlow<Double> = repository.getTotalDebt()
        .map { it ?: 0.0 } // Handle null jadi 0.0
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // 3. Total Piutang (Orang berutang ke kita)
    val totalReceivable: StateFlow<Double> = repository.getTotalReceivable()
        .map { it ?: 0.0 } // Handle null jadi 0.0
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Fungsi Simpan Utang Baru
    fun saveDebt(personName: String, amount: Double, type: String, description: String) {
        viewModelScope.launch {
            // 1. Simpan data utang/piutang
            val newDebt = DebtEntity(
                personName = personName,
                amount = amount,
                type = type, // "DEBT" atau "RECEIVABLE"
                description = description,
                isPaid = false, // Default belum lunas
                creationDate = System.currentTimeMillis()
            )
            repository.insertDebt(newDebt)

            // 2. Buat transaksi otomatis yang sesuai
            when (type) {
                "RECEIVABLE" -> { // Kita memberi pinjaman -> Uang Keluar
                    saveTransaction(
                        amount = amount,
                        type = "EXPENSE",
                        category = "Piutang",
                        description = "Memberi pinjaman ke $personName"
                    )
                }
                "DEBT" -> { // Kita menerima pinjaman -> Uang Masuk
                    saveTransaction(
                        amount = amount,
                        type = "INCOME",
                        category = "Utang",
                        description = "Menerima pinjaman dari $personName"
                    )
                }
            }
        }
    }

    // Fungsi Tandai Lunas / Belum Lunas (Toggle)
    fun toggleDebtStatus(debt: DebtEntity) {
        viewModelScope.launch {
            val updatedDebt = debt.copy(isPaid = !debt.isPaid)
            repository.updateDebt(updatedDebt)

            // Jika status BARU adalah LUNAS, buat transaksi
            if (updatedDebt.isPaid) {
                when (debt.type) {
                    "RECEIVABLE" -> { // Orang bayar utangnya ke kita -> Uang Masuk
                        saveTransaction(
                            amount = debt.amount,
                            type = "INCOME",
                            category = "Pelunasan Piutang",
                            description = "Pelunasan dari ${debt.personName}"
                        )
                    }
                    "DEBT" -> { // Kita bayar utang -> Uang Keluar
                        saveTransaction(
                            amount = debt.amount,
                            type = "EXPENSE",
                            category = "Pembayaran Utang",
                            description = "Membayar utang ke ${debt.personName}"
                        )
                    }
                }
            }
        }
    }

    // Fungsi Hapus Utang
    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }
}

// Factory (BARU: Disederhanakan)
class TransactionViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}