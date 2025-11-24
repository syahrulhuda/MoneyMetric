package com.example.moneymetric.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymetric.data.local.TransactionEntity
import com.example.moneymetric.ui.viewmodel.TransactionViewModel
// Pastikan file DashboardMenuOverlay sudah dibuat di folder ui/components
import com.example.moneymetric.ui.components.DashboardMenuOverlay
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TransactionViewModel,
    onNavigateToInput: (String) -> Unit // Sekarang menerima String (Route)
) {
    // Data Live dari ViewModel
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val initialCapital by viewModel.initialCapitalState.collectAsState()

    // Perhitungan Logika
    val profit = totalIncome - totalExpense
    val remainingCapital = initialCapital - profit
    val isBreakEven = initialCapital > 0 && profit >= initialCapital
    val progress = if (initialCapital > 0) (profit / initialCapital).toFloat() else 0f

    // State untuk Menu Overlay
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MoneyMetric", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMenu = true }, // 1. Buka Menu saat diklik
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
            }
        }
    ) { innerPadding ->

        // BOX UTAMA: Wadah untuk menumpuk Dashboard (Bawah) dan Overlay (Atas)
        Box(modifier = Modifier.fillMaxSize()) {

            // --- LAPISAN 1: KONTEN DASHBOARD ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 1. KARTU BALIK MODAL
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Status Balik Modal", fontSize = 14.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            if (isBreakEven) {
                                Text(
                                    "🎉 SUDAH BALIK MODAL!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text("Untung Bersih: ${formatRupiah(profit - initialCapital)}", color = Color.Black)
                            } else {
                                Text(
                                    "Kurang: ${formatRupiah(remainingCapital)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { progress.coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = Color(0xFF4CAF50),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Modal Kembali: ${(progress * 100).toInt()}%", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // 2. SUMMARY CARDS
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummaryCard(
                            title = "Pemasukan",
                            amount = totalIncome,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Pengeluaran",
                            amount = totalExpense,
                            color = Color(0xFFE53935),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. HEADER RIWAYAT
                item {
                    Text(
                        "Riwayat Transaksi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // LIST ITEM TRANSAKSI
                items(transactions) { transaction ->
                    TransactionItem(transaction = transaction) {
                        viewModel.deleteTransaction(transaction)
                    }
                }

                // Spacer Bawah
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            // --- LAPISAN 2: MENU OVERLAY (Muncul di atas jika showMenu = true) ---
            if (showMenu) {
                DashboardMenuOverlay(
                    onDismiss = { showMenu = false }, // Tutup jika klik luar
                    onNavigate = { route ->
                        showMenu = false // Tutup menu dulu
                        onNavigateToInput(route) // Pindah halaman (kirim string route)
                    }
                )
            }
        }
    }
}

// --- KOMPONEN HELPER (Tetap sama) ---

@Composable
fun SummaryCard(title: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(
                formatRupiah(amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity, onDelete: () -> Unit) {
    val isIncome = transaction.type == "INCOME"
    val color = if (isIncome) Color(0xFF4CAF50) else Color(0xFFE53935)
    val icon = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.category, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    transaction.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Text(
                (if (isIncome) "+ " else "- ") + formatRupiah(transaction.amount),
                fontWeight = FontWeight.Bold,
                color = color
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
            }
        }
    }
}

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace("Rp", "Rp ").substringBefore(",00")
}