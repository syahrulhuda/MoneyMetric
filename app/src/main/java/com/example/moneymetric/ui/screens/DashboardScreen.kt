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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymetric.data.local.DebtEntity
import com.example.moneymetric.data.local.TransactionEntity
import com.example.moneymetric.ui.viewmodel.TransactionViewModel
import com.example.moneymetric.ui.components.DashboardMenuOverlay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TransactionViewModel,
    onNavigateToInput: (String) -> Unit
) {
    // Data Live dari ViewModel
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val totalDebt by viewModel.totalDebt.collectAsState()
    val totalReceivable by viewModel.totalReceivable.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val debts by viewModel.allDebts.collectAsState() // Mengambil data utang
    val initialCapital by viewModel.initialCapitalState.collectAsState()

    // Perhitungan Logika
    val profit = totalIncome - totalExpense
    val remainingCapital = initialCapital - profit
    val isBreakEven = initialCapital > 0 && profit >= initialCapital
    val progress = if (initialCapital > 0) (profit / initialCapital).toFloat() else 0f

    // State untuk Menu Overlay & Tabs
    var showMenu by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Riwayat Transaksi", "Riwayat Utang")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MoneyMetric", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { onNavigateToInput("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMenu = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // KONTEN BAGIAN ATAS (SCROLLABLE)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Agar LazyColumn mengisi ruang yang tersedia
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Spacer atas
                    item { Spacer(modifier = Modifier.height(16.dp)) }

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

                    // 2. SUMMARY CARDS - Pemasukan & Pengeluaran
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

                    // 3. SUMMARY CARDS - Utang & Piutang
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SummaryCard(
                                title = "Total Utang",
                                amount = totalDebt,
                                color = Color(0xFFFF9800), // Orange
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Total Piutang",
                                amount = totalReceivable,
                                color = Color(0xFF2196F3), // Blue
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 4. TAB LAYOUT
                    item {
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(title) }
                                )
                            }
                        }
                    }

                    // 5. KONTEN TAB (HISTORI)
                    when (selectedTabIndex) {
                        0 -> items(transactions) { transaction ->
                            TransactionItem(transaction = transaction) {
                                viewModel.deleteTransaction(transaction)
                            }
                        }
                        1 -> items(debts) { debt ->
                            DebtItem(debt = debt, viewModel = viewModel)
                        }
                    }

                    // Spacer Bawah
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            // --- LAPISAN 2: MENU OVERLAY (Muncul di atas jika showMenu = true) ---
            if (showMenu) {
                DashboardMenuOverlay(
                    onDismiss = { showMenu = false },
                    onNavigate = { route ->
                        showMenu = false
                        onNavigateToInput(route)
                    }
                )
            }
        }
    }
}

// --- KOMPONEN HELPER ---

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

@Composable
fun DebtItem(debt: DebtEntity, viewModel: TransactionViewModel) {
    val isReceivable = debt.type == "RECEIVABLE"
    val amountColor = if (isReceivable) Color(0xFF2196F3) else Color(0xFFFF9800)
    val statusText = if (debt.isPaid) "LUNAS" else "BELUM LUNAS"
    val statusColor = if (debt.isPaid) Color.Gray else amountColor

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(debt.personName, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        formatRupiah(debt.amount),
                        fontWeight = FontWeight.Bold,
                        color = amountColor,
                        fontSize = 18.sp
                    )
                    if (debt.description.isNotEmpty()) {
                        Text(debt.description, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        statusText,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        formatDate(debt.creationDate),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                // Tombol Lunas/Batal Lunas
                TextButton(onClick = { viewModel.toggleDebtStatus(debt) }) {
                    Icon(
                        if (debt.isPaid) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (debt.isPaid) "Batal Lunas" else "Tandai Lunas")
                }
                // Tombol Hapus
                IconButton(onClick = { viewModel.deleteDebt(debt) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
                }
            }
        }
    }
}

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace("Rp", "Rp ").substringBefore(",00")
}

fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    return formatter.format(Date(millis))
}