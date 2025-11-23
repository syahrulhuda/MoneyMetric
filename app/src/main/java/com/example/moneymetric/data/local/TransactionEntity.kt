package com.example.moneymetric.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID unik otomatis (1, 2, 3...)

    val amount: Double,             // Jumlah uang (Rp)
    val type: String,               // "INCOME" atau "EXPENSE"
    val category: String,           // Kategori (Makan, Gaji, dll)
    val description: String,        // Catatan tambahan
    val dateInMillis: Long          // Tanggal transaksi
)