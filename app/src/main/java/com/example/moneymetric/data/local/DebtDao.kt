package com.example.moneymetric.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    // Simpan atau Update Utang
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity)

    // Update status (misal: tandai lunas)
    @Update
    suspend fun updateDebt(debt: DebtEntity)

    // Hapus utang
    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    // Ambil SEMUA data utang/piutang (diurutkan dari yang belum lunas, lalu tanggal)
    @Query("SELECT * FROM debts ORDER BY isPaid ASC, id DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    // Hitung Total UTANG SAYA (yang belum lunas)
    @Query("SELECT SUM(amount) FROM debts WHERE type = 'DEBT' AND isPaid = 0")
    fun getTotalDebt(): Flow<Double?>

    // Hitung Total ORANG UTANG KE SAYA (yang belum lunas)
    @Query("SELECT SUM(amount) FROM debts WHERE type = 'RECEIVABLE' AND isPaid = 0")
    fun getTotalReceivable(): Flow<Double?>
}