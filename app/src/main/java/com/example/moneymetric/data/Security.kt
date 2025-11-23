package com.example.moneymetric.data

data class Security(
    val useBiometrics: Boolean,
    val usePin: Boolean,
    val pin: String?
)
