package com.example.moneymetric.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: SettingEntity)

    @Query("SELECT * FROM settings WHERE `key` = :key")
    fun getSetting(key: String): Flow<SettingEntity?>
}
