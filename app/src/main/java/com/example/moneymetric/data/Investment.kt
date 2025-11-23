package com.example.moneymetric.data

import java.util.Date

data class Investment(
    val id: String,
    val name: String,
    val type: String,
    val quantity: Double,
    val purchasePrice: Double,
    val currentPrice: Double,
    val purchaseDate: Date
)
