package com.example.moneymetric.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymetric.data.local.BackupRestoreManager
import com.example.moneymetric.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val backupManager = remember { BackupRestoreManager(context) }

    // --- LAUNCHER UNTUK BACKUP (Simpan File) ---
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/x-sqlite3")
    ) { uri ->
        uri?.let { backupManager.backupData(it) }
    }

    // --- LAUNCHER UNTUK RESTORE (Buka File) ---
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            backupManager.restoreData(it)

            // --- KODE PENTING UNTUK RESTART APLIKASI ---
            // Dapatkan package manager
            val packageManager = context.packageManager
            // Buat intent untuk membuka launcher activity (activity utama)
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            val componentName = intent!!.component
            // Buat intent yang akan me-restart task
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            // Jalankan intent
            context.startActivity(mainIntent)
            // Matikan proses lama
            Runtime.getRuntime().exit(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp) // Jarak antar kartu lebih lega
        ) {

            // --- BAGIAN 1: KEAMANAN DATA ---
            Text(
                "Keamanan Data",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp) // Shadow lebih tebal biar pop-up
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Backup & Restore", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    // PERBAIKAN UI: Text warna DarkGray agar tidak pudar
                    Text(
                        "Simpan data keuangan Anda ke Google Drive atau penyimpanan HP agar aman.",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // TOMBOL BACKUP: Solid Green (Lebih Jelas)
                        Button(
                            onClick = {
                                val fileName = "MoneyMetric_Backup_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.db"
                                backupLauncher.launch(fileName)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Backup", fontWeight = FontWeight.Bold)
                        }

                        // TOMBOL RESTORE: Outlined tapi border jelas
                        OutlinedButton(
                            onClick = {
                                // Filter hanya file db atau semua file
                                restoreLauncher.launch(arrayOf("*/*"))
                            },
                            modifier = Modifier.weight(1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Restore")
                        }
                    }
                }
            }

            // --- BAGIAN 2: UMUM ---
            Text(
                "Umum",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Modal Awal", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Ubah modal awal jika ada suntikan dana baru.",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Tombol ini masih dummy, nanti bisa ditambah dialog
                    Button(
                        onClick = { /* Nanti tambah logika edit modal */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Edit Modal Awal")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("MoneyMetric v1.0", color = Color.Gray, fontSize = 12.sp)
                Text("Dibuat untuk Tugas Kewirausahaan", color = Color.LightGray, fontSize = 10.sp)
            }
        }
    }
}