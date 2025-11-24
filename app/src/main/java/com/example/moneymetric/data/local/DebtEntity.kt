package com.example.moneymetric.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,         // Nama orang (misal: "Bu Siti")
    val amount: Double,             // Jumlah utang
    val type: String,               // "PAYABLE" (Kita ngutang) atau "RECEIVABLE" (Orang ngutang)
    val dueDate: Long?,             // Tanggal jatuh tempo (opsional)
    val isPaid: Boolean = false,    // Status lunas?
    val description: String         // Catatan (misal: "Beli Gula")
)