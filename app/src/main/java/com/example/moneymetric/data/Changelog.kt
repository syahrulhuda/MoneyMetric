package com.example.moneymetric.data

import java.util.Date

data class Changelog(
    val version: String,
    val date: Date,
    val changes: List<String>
)
