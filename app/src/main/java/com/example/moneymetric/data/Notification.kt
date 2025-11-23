package com.example.moneymetric.data

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val date: Date,
    val isRead: Boolean
)
