package com.example.moneymetric.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,         // Nama orang (misal: "Bu Siti")
    val amount: Double,             // Jumlah utang
    val type: String,               // "DEBT" atau "RECEIVABLE"
    val isPaid: Boolean = false,    // Status lunas?
    val description: String,        // Catatan (misal: "Beli Gula")
    val creationDate: Long          // Tanggal dibuat
)