package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekExpense
import com.example.ui.theme.SleekExpenseBg
import com.example.ui.theme.SleekExpenseBorder
import com.example.ui.theme.SleekIncome
import com.example.ui.theme.SleekIncomeBg
import com.example.ui.theme.SleekIncomeBorder
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCategoryIcon(category: String): ImageVector {
    val lower = category.lowercase()
    return when {
        lower.contains("makan") || lower.contains("minum") || lower.contains("resto") || lower.contains("kuliner") -> Icons.Default.Restaurant
        lower.contains("gaji") || lower.contains("honor") || lower.contains("income") -> Icons.Default.Work
        lower.contains("belanja") || lower.contains("shop") || lower.contains("pasar") || lower.contains("mall") -> Icons.Default.ShoppingBag
        lower.contains("transport") || lower.contains("bensin") || lower.contains("ojek") || lower.contains("kendaraan") -> Icons.Default.DirectionsCar
        lower.contains("tagihan") || lower.contains("listrik") || lower.contains("air") || lower.contains("wifi") || lower.contains("pulsa") -> Icons.Default.Receipt
        lower.contains("obat") || lower.contains("sehat") || lower.contains("dokter") || lower.contains("rs") -> Icons.Default.MedicalServices
        lower.contains("hadiah") || lower.contains("gift") || lower.contains("bonus") -> Icons.Default.CardGiftcard
        else -> Icons.Default.Payments
    }
}

@Composable
fun TransactionItemCard(
    index: Int,
    item: TransactionItem,
    onEdit: (TransactionItem) -> Unit,
    onDelete: (TransactionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val isIncome = item.type == TransactionType.INCOME
    val textColor = if (isIncome) SleekIncome else SleekExpense
    val badgeBg = if (isIncome) SleekIncomeBg else SleekExpenseBg
    val badgeBorder = if (isIncome) SleekIncomeBorder else SleekExpenseBorder
    val badgeText = if (isIncome) "IN" else "OUT"

    val dateFormatted = remember(item.dateMillis) {
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        sdf.format(Date(item.dateMillis))
    }

    // Sleek Interface Table Row Item
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEdit(item) }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("transaction_item_${item.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Column: Tanggal (e.g. "24/05")
            Text(
                text = dateFormatted,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = SleekTextSecondary,
                    fontSize = 12.sp
                ),
                modifier = Modifier.width(42.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 2. Column: Jenis & Keterangan
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SleekTextPrimary,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.note.isNotBlank()) {
                    Text(
                        text = item.note,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SleekTextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. Column: Harga
            val sign = if (isIncome) "+" else "-"
            Text(
                text = "$sign${CurrencyFormatter.formatRupiah(item.amount)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 13.sp
                ),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 4. Column: Tipe Badge (IN / OUT pill)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .border(1.dp, badgeBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        fontSize = 10.sp
                    )
                )
            }

            // Quick Menu Button
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Pilihan Transaksi",
                        tint = SleekTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Transaksi") },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onEdit(item)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus Transaksi", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            showMenu = false
                            onDelete(item)
                        }
                    )
                }
            }
        }
    }
}

