package com.example.moneymetric.data

import java.util.Date

data class Bill(
    val id: String,
    val name: String,
    val amount: Double,
    val dueDate: Date,
    val isPaid: Boolean
)
