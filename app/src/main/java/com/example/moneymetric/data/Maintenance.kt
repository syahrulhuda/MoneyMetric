package com.example.moneymetric.data

import java.util.Date

data class Maintenance(
    val isUnderMaintenance: Boolean,
    val message: String,
    val startDate: Date,
    val endDate: Date
)
