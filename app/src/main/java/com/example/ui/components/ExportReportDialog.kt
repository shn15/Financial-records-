package com.example.ui.components

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TransactionItem
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekExpense
import com.example.ui.theme.SleekHeroContainer
import com.example.ui.theme.SleekHeroOnContainer
import com.example.ui.theme.SleekIncome
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSecondaryContainer
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.util.CurrencyFormatter

@Composable
fun ExportReportDialog(
    monthName: String,
    transactions: List<TransactionItem>,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onSaveToUri: (android.net.Uri) -> Unit
) {
    val context = LocalContext.current
    val totalIncome = remember(transactions) {
        transactions.filter { it.type.name == "INCOME" }.sumOf { it.amount }
    }
    val totalExpense = remember(transactions) {
        transactions.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
    }
    val netBalance = totalIncome - totalExpense
    val totalNominal = remember(transactions) {
        transactions.sumOf { it.amount }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            onSaveToUri(uri)
        }
    }

    val defaultFileName = remember(monthName) {
        val sanitized = monthName.replace(" ", "_").replace("/", "_")
        "Laporan_Keuangan_${sanitized}.csv"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
                .testTag("export_report_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SleekHeroContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = null,
                                tint = SleekHeroOnContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Laporan Excel / CSV",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextPrimary
                                )
                            )
                            Text(
                                text = "Periode: $monthName",
                                style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = SleekTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Summary Section Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SleekSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "RINGKASAN SALDO PER BULAN",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SleekHeroOnContainer,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Uang Masuk:", style = MaterialTheme.typography.bodyMedium, color = SleekTextPrimary)
                                Text(
                                    CurrencyFormatter.formatRupiah(totalIncome),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SleekIncome
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Uang Keluar:", style = MaterialTheme.typography.bodyMedium, color = SleekTextPrimary)
                                Text(
                                    CurrencyFormatter.formatRupiah(totalExpense),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SleekExpense
                                    )
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SleekBorder.copy(alpha = 0.5f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Sisa Saldo Akhir:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SleekTextPrimary
                                )
                                Text(
                                    CurrencyFormatter.formatRupiah(netBalance),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (netBalance >= 0) SleekIncome else SleekExpense
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Transaction Table Preview
                    Text(
                        text = "KOLOM TABEL TRANSAKSI (${transactions.size} Data)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Horizontal scrolling table representation
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(12.dp)
                        ) {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SleekHeroContainer)
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("No", Modifier.width(36.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = SleekHeroOnContainer)
                                Text("Tanggal", Modifier.width(130.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = SleekHeroOnContainer)
                                Text("Jenis / Kategori", Modifier.width(130.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = SleekHeroOnContainer)
                                Text("Harga Barang", Modifier.width(110.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = SleekHeroOnContainer)
                                Text("Jenis Transaksi", Modifier.width(100.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = SleekHeroOnContainer)
                                Text("Keterangan", Modifier.width(160.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = SleekHeroOnContainer)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Table Rows
                            if (transactions.isEmpty()) {
                                Text(
                                    text = "Belum ada transaksi di bulan ini.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SleekTextSecondary
                                )
                            } else {
                                transactions.sortedBy { it.dateMillis }.forEachIndexed { index, item ->
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${index + 1}", Modifier.width(36.dp), style = MaterialTheme.typography.bodySmall, color = SleekTextPrimary)
                                        Text(CurrencyFormatter.formatDate(item.dateMillis), Modifier.width(130.dp), style = MaterialTheme.typography.bodySmall, color = SleekTextPrimary)
                                        Text(item.category, Modifier.width(130.dp), fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall, color = SleekTextPrimary)
                                        Text(CurrencyFormatter.formatRupiah(item.amount), Modifier.width(110.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = if (item.type.name == "INCOME") SleekIncome else SleekExpense)
                                        Text(item.type.exportLabel, Modifier.width(100.dp), style = MaterialTheme.typography.bodySmall, color = SleekTextSecondary)
                                        Text(item.note.ifBlank { "-" }, Modifier.width(160.dp), style = MaterialTheme.typography.bodySmall, color = SleekTextSecondary)
                                    }
                                    HorizontalDivider(color = SleekBorder.copy(alpha = 0.4f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Final Total Rekapitulasi Otomatis Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekSecondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "TOTAL REKAPITULASI OTOMATIS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Semua Nominal Transaksi:", style = MaterialTheme.typography.bodySmall, color = SleekTextPrimary)
                                Text(
                                    CurrencyFormatter.formatRupiah(totalNominal),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onShare,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("share_csv_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bagikan Laporan (Excel / CSV)", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            createDocumentLauncher.launch(defaultFileName)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_csv_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan File ke Perangkat", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

