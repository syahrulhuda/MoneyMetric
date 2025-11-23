package com.example.moneymetric.data

import java.util.Date

data class Reminder(
    val id: String,
    val title: String,
    val date: Date,
    val isCompleted: Boolean
)
