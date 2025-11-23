package com.example.moneymetric.data

import java.util.Date

data class Sync(
    val lastSyncDate: Date,
    val syncFrequency: String
)
