package com.example.moneymetric.data

import java.util.Date

data class NetWorth(
    val id: String,
    val date: Date,
    val totalAssets: Double,
    val totalLiabilities: Double,
    val netWorth: Double
)
