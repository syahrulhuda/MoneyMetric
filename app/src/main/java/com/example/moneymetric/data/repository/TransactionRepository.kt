package com.example.moneymetric.data.repository

import com.example.moneymetric.data.local.DebtDao
import com.example.moneymetric.data.local.DebtEntity
import com.example.moneymetric.data.local.TransactionDao
import com.example.moneymetric.data.local.TransactionEntity
import com.example.moneymetric.data.local.KEY_INITIAL_CAPITAL
import com.example.moneymetric.data.local.SettingDao
import com.example.moneymetric.data.local.SettingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val debtDao: DebtDao,
    private val settingDao: SettingDao
) {

    // --- BAGIAN TRANSAKSI ---
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

    // --- BAGIAN UTANG/PIUTANG ---

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
    
    // --- BAGIAN MODAL AWAL ---

    fun getInitialCapital(): Flow<Double?> {
        return settingDao.getSetting(KEY_INITIAL_CAPITAL).map { it?.value?.toDoubleOrNull() }
    }

    suspend fun setInitialCapital(amount: Double) {
        val setting = SettingEntity(key = KEY_INITIAL_CAPITAL, value = amount.toString())
        settingDao.saveSetting(setting)
    }
}