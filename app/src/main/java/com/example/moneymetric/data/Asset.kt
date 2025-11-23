package com.example.moneymetric.data

import java.util.Date

data class Asset(
    val id: String,
    val name: String,
    val value: Double,
    val purchaseDate: Date
)
