package com.example.moneymetric.data

import java.util.Date

data class Project(
    val name: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val url: String
)
