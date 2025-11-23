package com.example.moneymetric.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // Fungsi untuk menambah transaksi baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Fungsi untuk menghapus transaksi
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // Fungsi untuk mengambil SEMUA transaksi, diurutkan dari yang paling baru
    // Kita pakai 'Flow' agar datanya update otomatis (Live) ke Dashboard
    @Query("SELECT * FROM transactions ORDER BY dateInMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    // Fungsi untuk menghitung total pemasukan (INCOME)
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    // Fungsi untuk menghitung total pengeluaran (EXPENSE)
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double?>
}