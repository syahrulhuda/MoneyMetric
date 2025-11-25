package com.example.moneymetric.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val personName: String,
    val amount: Double,
    val type: String,
    val isPaid: Boolean = false,
    val description: String,
    val creationDate: Long
)