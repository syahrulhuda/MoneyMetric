package com.example.moneymetric.data

import java.util.Date

data class RecurringTransaction(
    val id: String,
    val accountId: String,
    val amount: Double,
    val description: String,
    val category: String,
    val frequency: String,
    val startDate: Date,
    val endDate: Date?
)
