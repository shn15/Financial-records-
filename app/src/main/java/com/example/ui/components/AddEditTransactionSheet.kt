package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekExpense
import com.example.ui.theme.SleekIncome
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSecondaryContainer
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.util.CurrencyFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionSheet(
    initialItem: TransactionItem?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        dateMillis: Long,
        category: String,
        amount: Double,
        type: TransactionType,
        note: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var transactionType by remember {
        mutableStateOf(initialItem?.type ?: TransactionType.EXPENSE)
    }
    var amountText by remember {
        mutableStateOf(if (initialItem != null) initialItem.amount.toLong().toString() else "")
    }
    var category by remember {
        mutableStateOf(initialItem?.category ?: "")
    }
    var note by remember {
        mutableStateOf(initialItem?.note ?: "")
    }
    var dateMillis by remember {
        mutableLongStateOf(initialItem?.dateMillis ?: System.currentTimeMillis())
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val expenseCategories = remember {
        listOf(
            "Makanan & Minuman",
            "Belanja Kebutuhan",
            "Transportasi",
            "Tagihan & Utilitas",
            "Hiburan & Rekreasi",
            "Kesehatan & Obat",
            "Pendidikan",
            "Peralatan & Barang",
            "Sedekah / Donasi",
            "Lainnya"
        )
    }

    val incomeCategories = remember {
        listOf(
            "Gaji & Upah",
            "Hasil Penjualan",
            "Bisnis & Usaha",
            "Bonus & THR",
            "Investasi & Dividen",
            "Hadiah / Hibah",
            "Pengembalian Dana",
            "Lainnya"
        )
    }

    val quickAmounts = remember {
        listOf(10000L, 25000L, 50000L, 100000L, 250000L, 500000L, 1000000L)
    }

    val cal = remember(dateMillis) {
        Calendar.getInstance().apply { timeInMillis = dateMillis }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = SleekSurface,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialItem == null) "Tambah Transaksi" else "Edit Transaksi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = SleekTextPrimary
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = SleekTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transaction Type Toggle (Masuk vs Keluar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekSurfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Pengeluaran / Keluar
                val isExpense = transactionType == TransactionType.EXPENSE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isExpense) SleekExpense else Color.Transparent)
                        .clickable {
                            transactionType = TransactionType.EXPENSE
                            if (category.isBlank() || incomeCategories.contains(category)) {
                                category = expenseCategories.first()
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isExpense) Color.White else SleekTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Uang Keluar",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isExpense) Color.White else SleekTextSecondary
                            )
                        )
                    }
                }

                // Pemasukan / Masuk
                val isIncome = transactionType == TransactionType.INCOME
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isIncome) SleekIncome else Color.Transparent)
                        .clickable {
                            transactionType = TransactionType.INCOME
                            if (category.isBlank() || expenseCategories.contains(category)) {
                                category = incomeCategories.first()
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isIncome) Color.White else SleekTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Uang Masuk",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isIncome) Color.White else SleekTextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nominal / Harga Barang Input
            Text(
                text = "Harga Barang / Nominal (Rp)",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SleekTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    amountText = filtered
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_input"),
                placeholder = { Text("0") },
                prefix = {
                    Text(
                        "Rp ",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (transactionType == TransactionType.INCOME) SleekIncome else SleekExpense
                        )
                    )
                },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (transactionType == TransactionType.INCOME) SleekIncome else SleekExpense
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage != null
            )

            // Quick Amount Chips
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickAmounts.forEach { quickVal ->
                    val quickLabel = if (quickVal >= 1000000L) {
                        "${quickVal / 1000000L} Jt"
                    } else {
                        "${quickVal / 1000L} Rb"
                    }
                    Surface(
                        onClick = {
                            val current = amountText.toLongOrNull() ?: 0L
                            amountText = (current + quickVal).toString()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = SleekSurfaceVariant
                    ) {
                        Text(
                            text = "+$quickLabel",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SleekTextPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Jenis / Kategori
            Text(
                text = "Jenis / Kategori",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SleekTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("category_input"),
                placeholder = { Text("Pilih atau ketik jenis barang/transaksi") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Sell, contentDescription = null, tint = SleekTextSecondary)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category Chips
            Spacer(modifier = Modifier.height(8.dp))
            val currentList = if (transactionType == TransactionType.INCOME) incomeCategories else expenseCategories
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                currentList.forEach { catName ->
                    val isSelected = category.equals(catName, ignoreCase = true)
                    Surface(
                        onClick = { category = catName },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) SleekSecondaryContainer else SleekSurfaceVariant
                    ) {
                        Text(
                            text = catName,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = SleekTextPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tanggal & Waktu
            Text(
                text = "Tanggal Transaksi",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SleekTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Date picker trigger
                Surface(
                    onClick = {
                        val year = cal.get(Calendar.YEAR)
                        val month = cal.get(Calendar.MONTH)
                        val day = cal.get(Calendar.DAY_OF_MONTH)
                        DatePickerDialog(context, { _, y, m, d ->
                            val newCal = Calendar.getInstance().apply {
                                timeInMillis = dateMillis
                                set(Calendar.YEAR, y)
                                set(Calendar.MONTH, m)
                                set(Calendar.DAY_OF_MONTH, d)
                            }
                            dateMillis = newCal.timeInMillis
                        }, year, month, day).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = SleekSurfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = SleekPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = CurrencyFormatter.formatShortDate(dateMillis),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = SleekTextPrimary
                            )
                        )
                    }
                }

                // Time picker trigger
                Surface(
                    onClick = {
                        val hour = cal.get(Calendar.HOUR_OF_DAY)
                        val minute = cal.get(Calendar.MINUTE)
                        TimePickerDialog(context, { _, h, m ->
                            val newCal = Calendar.getInstance().apply {
                                timeInMillis = dateMillis
                                set(Calendar.HOUR_OF_DAY, h)
                                set(Calendar.MINUTE, m)
                            }
                            dateMillis = newCal.timeInMillis
                        }, hour, minute, true).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = SleekSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = SleekPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val timeStr = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                        Text(
                            text = timeStr,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = SleekTextPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Keterangan / Catatan
            Text(
                text = "Keterangan (Opsional)",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SleekTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_input"),
                placeholder = { Text("Contoh: Beli tiket kereta, Belanja di supermarket") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = SleekTextSecondary)
                },
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val amountVal = amountText.toDoubleOrNull()
                    if (amountVal == null || amountVal <= 0) {
                        errorMessage = "Harap masukkan nominal harga yang valid!"
                        return@Button
                    }
                    val finalCategory = if (category.isNotBlank()) {
                        category
                    } else if (transactionType == TransactionType.INCOME) {
                        "Pemasukan Lainnya"
                    } else {
                        "Pengeluaran Lainnya"
                    }

                    onSave(
                        initialItem?.id ?: 0L,
                        dateMillis,
                        finalCategory,
                        amountVal,
                        transactionType,
                        note
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_transaction_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekPrimary
                )
            ) {
                Text(
                    text = if (initialItem == null) "Simpan Transaksi" else "Perbarui Transaksi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

