package com.example.moneymetric.data

data class Export(
    val format: String,
    val includeTransactions: Boolean,
    val includeInvestments: Boolean
)
