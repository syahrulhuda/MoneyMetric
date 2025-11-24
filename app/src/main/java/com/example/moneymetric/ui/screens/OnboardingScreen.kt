package com.example.moneymetric.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.moneymetric.R
import com.example.moneymetric.ui.CurrencyAmountInputVisualTransformation
import com.example.moneymetric.ui.viewmodel.TransactionViewModel

@Composable
fun OnboardingScreen(
    viewModel: TransactionViewModel,
    onFinish: () -> Unit
) {
    var capitalInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.moneymetriclogo),
            contentDescription = "Logo Aplikasi",
            modifier = Modifier
                .size(45.dp)
                .padding(bottom = 16.dp)
        )
        Text("Selamat Datang di MoneyMetric", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Measure your Money Usage", fontSize = 15.sp)

        Spacer(modifier = Modifier.height(25.dp))

        Text("Berapa Modal Awal Anda?", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = capitalInput,
            onValueChange = { if (it.all { char -> char.isDigit() }) capitalInput = it },
            label = { Text("Masukkan Modal (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CurrencyAmountInputVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val capital = capitalInput.toDoubleOrNull() ?: 0.0
                viewModel.setInitialCapital(capital) // Simpan ke DataStore
                onFinish() // Pindah ke Dashboard
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = capitalInput.isNotEmpty()
        ) {
            Text("Mulai Sekarang")
        }

        TextButton(onClick = {
            viewModel.setInitialCapital(0.0) // Set 0 jika dilewati
            onFinish()
        }) {
            Text("Lewati (Isi Nanti)")
        }
    }
}