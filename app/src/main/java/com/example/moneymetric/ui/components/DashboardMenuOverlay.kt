package com.example.moneymetric.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardMenuOverlay(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit
) {
    // Box utama untuk background gelap transparan
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)) // Efek Gelap
            .clickable { onDismiss() }, // Klik luar untuk tutup menu
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // --- BARIS 1: Pemasukan & Pengeluaran ---
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                MenuButton(
                    text = "Uang\nMasuk",
                    color = Color(0xFF4CAF50), // Hijau
                    icon = Icons.Default.ArrowUpward,
                    onClick = { onNavigate("input_transaction/income") }
                )
                MenuButton(
                    text = "Uang\nKeluar",
                    color = Color(0xFFE53935), // Merah
                    icon = Icons.Default.ArrowDownward,
                    onClick = { onNavigate("input_transaction/expense") }
                )
            }

            // --- BARIS 2: Utang & Piutang ---
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                MenuButton(
                    text = "Catat\nUtang",
                    color = Color(0xFFFF9800), // Oranye
                    icon = Icons.Default.List,
                    onClick = { onNavigate("input_debt/debt") }
                )
                MenuButton(
                    text = "Catat\nPiutang",
                    color = Color(0xFF2196F3), // Biru
                    icon = Icons.Default.Add,
                    onClick = { onNavigate("input_debt/receivable") }
                )
            }
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        // Lingkaran Berwarna
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp) // Ukuran tombol
                .clip(CircleShape)
                .background(color)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Teks Label
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}