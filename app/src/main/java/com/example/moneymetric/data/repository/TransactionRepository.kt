package com.example.moneymetric.data.repository

import com.example.moneymetric.data.local.TransactionDao
import com.example.moneymetric.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    // Fungsi untuk menambah/edit transaksi
    suspend fun insertTransaction(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }

    // Fungsi untuk menghapus transaksi
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    // Mengambil semua data (Real-time update ke UI)
    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return dao.getAllTransactions()
    }

    // Mengambil total pemasukan
    fun getTotalIncome(): Flow<Double?> {
        return dao.getTotalIncome()
    }

    // Mengambil total pengeluaran
    fun getTotalExpense(): Flow<Double?> {
        return dao.getTotalExpense()
    }
}