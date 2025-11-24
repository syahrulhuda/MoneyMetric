package com.example.moneymetric.data.repository

import com.example.moneymetric.data.local.DebtDao
import com.example.moneymetric.data.local.DebtEntity
import com.example.moneymetric.data.local.TransactionDao
import com.example.moneymetric.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

// Perhatikan: Sekarang kita minta 2 DAO (TransactionDao DAN DebtDao)
class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val debtDao: DebtDao
) {

    // --- BAGIAN TRANSAKSI (TETAP SAMA) ---
    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    fun getTotalIncome(): Flow<Double?> {
        return transactionDao.getTotalIncome()
    }

    fun getTotalExpense(): Flow<Double?> {
        return transactionDao.getTotalExpense()
    }

    // --- BAGIAN UTANG/PIUTANG (BARU) ---

    suspend fun insertDebt(debt: DebtEntity) {
        debtDao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: DebtEntity) {
        debtDao.updateDebt(debt)
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        debtDao.deleteDebt(debt)
    }

    fun getAllDebts(): Flow<List<DebtEntity>> {
        return debtDao.getAllDebts()
    }

    fun getTotalDebt(): Flow<Double?> {
        // Menghitung total utang kita ke orang lain
        return debtDao.getTotalDebt()
    }

    fun getTotalReceivable(): Flow<Double?> {
        // Menghitung total orang lain utang ke kita
        return debtDao.getTotalReceivable()
    }
}