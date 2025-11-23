package com.example.moneymetric.data

import java.util.Date

data class Report(
    val id: String,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val data: Map<String, Double>
)
