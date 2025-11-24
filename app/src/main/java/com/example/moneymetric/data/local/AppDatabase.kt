package com.example.moneymetric.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class, DebtEntity::class, SettingEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Daftarkan DAO yang tadi kita buat
    abstract fun transactionDao(): TransactionDao
    abstract fun debtDao(): DebtDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moneymetric_database"
                )
                    // PENTING: Tambahkan ini agar tidak crash saat update tabel
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun closeInstance() {
            INSTANCE?.let {
                if (it.isOpen) {
                    it.close()
                }
            }
            INSTANCE = null
        }
    }
}