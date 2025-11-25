package com.example.moneymetric.data.local

import android.content.Context
import android.net.Uri
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class BackupRestoreManager(private val context: Context) {

    private val dbName = "moneymetric_database"

    fun backupData(uri: Uri) {
        try {
            // 1. TUTUP DATABASE UNTUK MEMASTIKAN FILE KONSISTEN
            AppDatabase.closeInstance()

            // 2. Cari file database asli di HP
            val dbFile = context.getDatabasePath(dbName)

            // 3. Buka aliran data (Stream)
            val inputStream = FileInputStream(dbFile)
            val outputStream = context.contentResolver.openOutputStream(uri)

            // 4. Salin data
            outputStream?.use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(context, "✅ Backup Berhasil Disimpan!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "❌ Gagal Backup: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun restoreData(uri: Uri) {
        try {
            // 1. TUTUP KONEKSI DATABASE
            AppDatabase.closeInstance()

            // 2. HAPUS SEMUA FILE DATABASE LAMA
            val dbFile = context.getDatabasePath(dbName)
            val walFile = File(dbFile.path + "-wal")
            val shmFile = File(dbFile.path + "-shm")

            if (dbFile.exists()) dbFile.delete()
            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            // 3. Buka stream dari file backup yang dipilih user
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(dbFile)

            // 4. Timpa database lama dengan yang baru
            outputStream.use { output ->
                inputStream?.use { input ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(context, "✅ Data Pulih! Aplikasi akan dimulai ulang.", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "❌ Gagal Restore: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
