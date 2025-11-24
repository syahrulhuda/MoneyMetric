package com.example.moneymetric.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

const val KEY_INITIAL_CAPITAL = "initial_capital"