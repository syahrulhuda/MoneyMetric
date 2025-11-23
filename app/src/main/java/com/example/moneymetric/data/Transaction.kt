package com.example.moneymetric.data

import java.util.Date

data class Transaction(
    val id: String,
    val accountId: String,
    val amount: Double,
    val date: Date,
    val description: String,
    val category: String
)
