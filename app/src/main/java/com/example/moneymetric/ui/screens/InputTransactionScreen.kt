package com.example.moneymetric.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.moneymetric.ui.viewmodel.TransactionViewModel
import com.example.moneymetric.ui.CurrencyAmountInputVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTransactionScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    // State untuk menyimpan input pengguna sementara
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(true) } // true = Pemasukan, false = Pengeluaran

    // Warna tema berdasarkan jenis transaksi (Hijau/Merah)
    val primaryColor = if (isIncome) Color(0xFF4CAF50) else Color(0xFFE53935)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isIncome) "Catat Pemasukan" else "Catat Pengeluaran") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Toggle Tombol (Pemasukan / Pengeluaran)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isIncome = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isIncome) Color(0xFF4CAF50) else Color.LightGray
                    )
                ) {
                    Text("Pemasukan")
                }
                Button(
                    onClick = { isIncome = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isIncome) Color(0xFFE53935) else Color.LightGray
                    )
                ) {
                    Text("Pengeluaran")
                }
            }

            // 2. Input Jumlah Uang
            OutlinedTextField(
                value = amount,
                onValueChange = { input ->
                    // Filter: Hanya angka, maksimal 13 digit biar ga error
                    if (input.all { it.isDigit() } && input.length <= 13) {
                        amount = input
                    }
                },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,

                // --- TAMBAHKAN BARIS INI ---
                visualTransformation = CurrencyAmountInputVisualTransformation()
            )

            // 3. Input Kategori
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori (Cth: Makan, Gaji, Bensin)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 4. Input Keterangan
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Keterangan (Opsional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // 5. Tombol SIMPAN
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    // Validasi sederhana: Jumlah > 0 dan Kategori tidak kosong
                    if (amountDouble > 0 && category.isNotEmpty()) {
                        viewModel.saveTransaction(
                            amount = amountDouble,
                            type = if (isIncome) "INCOME" else "EXPENSE",
                            category = category,
                            description = description
                        )
                        onNavigateBack() // Kembali ke Dashboard setelah simpan
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = amount.isNotEmpty() && category.isNotEmpty() // Matikan tombol jika kosong
            ) {
                Text("SIMPAN", color = Color.White)
            }
        }
    }
}