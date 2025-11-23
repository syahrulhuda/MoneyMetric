package com.example.moneymetric.data

import java.util.Date

data class Subscription(
    val id: String,
    val name: String,
    val amount: Double,
    val frequency: String,
    val nextBillingDate: Date
)
