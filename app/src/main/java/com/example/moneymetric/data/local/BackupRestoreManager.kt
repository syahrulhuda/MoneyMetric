package com.example.moneymetric.data.local

import android.content.Context
import android.net.Uri
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class BackupRestoreManager(private val context: Context) {

    // Nama database Room kita (sesuai di AppDatabase.kt)
    private val dbName = "moneymetric_database"

    fun backupData(uri: Uri) {
        try {
            // 1. Cari file database asli di HP
            val dbFile = context.getDatabasePath(dbName)

            // 2. Buka aliran data (Stream)
            val inputStream = FileInputStream(dbFile)
            val outputStream = context.contentResolver.openOutputStream(uri)

            // 3. Salin data
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
            // TUTUP KONEKSI DATABASE SEBELUM RESTORE
            AppDatabase.closeInstance()

            val dbFile = context.getDatabasePath(dbName)

            // 2. Buka stream dari file backup yang dipilih user
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(dbFile)

            // 3. Timpa database lama
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