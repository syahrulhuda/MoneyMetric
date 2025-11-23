package com.example.moneymetric.data

import java.util.Date

data class Liability(
    val id: String,
    val name: String,
    val amount: Double,
    val interestRate: Double,
    val dueDate: Date
)
