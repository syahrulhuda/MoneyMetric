package com.example.moneymetric.data

import java.util.Date

data class Goal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: Date
)
