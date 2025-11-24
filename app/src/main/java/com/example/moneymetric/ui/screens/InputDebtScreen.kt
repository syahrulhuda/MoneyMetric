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
import com.example.moneymetric.ui.CurrencyAmountInputVisualTransformation
import com.example.moneymetric.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDebtScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit,
    initialType: String? = null
) {
    // State Form
    var personName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isReceivable by remember { mutableStateOf(initialType != "debt") }
    var isSaving by remember { mutableStateOf(false) } // State untuk mencegah klik ganda
    val primaryColor = if (isReceivable) Color(0xFF2196F3) else Color(0xFFFF9800)
    val titleText = if (isReceivable) "Catat Piutang (Aset)" else "Catat Utang (Kewajiban)"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText) },
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
            // 1. Toggle Jenis (Piutang vs Utang)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isReceivable = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isReceivable) Color(0xFF2196F3) else Color.LightGray
                    )
                ) {
                    Text("Piutang")
                }
                Button(
                    onClick = { isReceivable = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isReceivable) Color(0xFFFF9800) else Color.LightGray
                    )
                ) {
                    Text("Utang Saya")
                }
            }

            // 2. Input Nama Orang (Wajib)
            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it },
                label = { Text(if (isReceivable) "Siapa yang berutang?" else "Berutang kepada siapa?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 3. Input Jumlah
            OutlinedTextField(
                value = amount,
                onValueChange = { input ->
                    if (input.all { it.isDigit() } && input.length <= 13) {
                        amount = input
                    }
                },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = CurrencyAmountInputVisualTransformation()
            )

            // 4. Catatan
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Catatan (Opsional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // 5. Tombol Simpan
            Button(
                onClick = {
                    if (isSaving) return@Button // Jika sedang menyimpan, abaikan klik
                    isSaving = true

                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    if (amountDouble > 0 && personName.isNotEmpty()) {
                        viewModel.saveDebt(
                            personName = personName,
                            amount = amountDouble,
                            type = if (isReceivable) "RECEIVABLE" else "DEBT",
                            description = description,
                            dueDate = null // Nanti bisa dikembangkan pakai DatePicker
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = amount.isNotEmpty() && personName.isNotEmpty() && !isSaving
            ) {
                Text("SIMPAN", color = Color.White)
            }
        }
    }
}