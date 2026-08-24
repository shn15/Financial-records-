package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TransactionType
import com.example.ui.components.AddEditTransactionSheet
import com.example.ui.components.ExportReportDialog
import com.example.ui.components.FinancialSummaryCard
import com.example.ui.components.MonthSelector
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.FinancialExpense
import com.example.ui.theme.FinancialIncome
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekHeroContainer
import com.example.ui.theme.SleekHeroOnContainer
import com.example.ui.theme.SleekOnPrimary
import com.example.ui.theme.SleekOnSecondaryContainer
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekSecondaryContainer
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedMonthYear by viewModel.selectedMonthYear.collectAsStateWithLifecycle()
    val monthlyTransactions by viewModel.monthlyTransactions.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()

    val isAddEditSheetOpen by viewModel.isAddEditSheetOpen.collectAsStateWithLifecycle()
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()
    val isExportDialogOpen by viewModel.isExportDialogOpen.collectAsStateWithLifecycle()
    val transactionToDelete by viewModel.transactionToDelete.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    var selectedNavTab by remember { mutableIntStateOf(0) }

    val totalIncome = remember(monthlyTransactions) {
        viewModel.calculateIncome(monthlyTransactions)
    }
    val totalExpense = remember(monthlyTransactions) {
        viewModel.calculateExpense(monthlyTransactions)
    }
    val monthName = viewModel.getMonthName()

    // Collect feedback toast/snackbars
    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SleekBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SleekHeroContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = SleekHeroOnContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Saldoku",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    letterSpacing = (-0.5).sp,
                                    color = SleekTextPrimary
                                )
                            )
                            Text(
                                text = monthName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = SleekTextSecondary
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Sleek circular action button
                    IconButton(
                        onClick = { viewModel.openExportDialog() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SleekSurfaceVariant)
                            .testTag("export_excel_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Pilihan Menu",
                            tint = SleekTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SleekBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SleekSurfaceVariant,
                modifier = Modifier.height(72.dp)
            ) {
                NavigationBarItem(
                    selected = selectedNavTab == 0,
                    onClick = { selectedNavTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 11.sp, fontWeight = if (selectedNavTab == 0) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = SleekSecondaryContainer,
                        selectedIconColor = SleekOnSecondaryContainer,
                        selectedTextColor = SleekOnSecondaryContainer,
                        unselectedIconColor = SleekTextSecondary,
                        unselectedTextColor = SleekTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedNavTab == 1,
                    onClick = {
                        selectedNavTab = 1
                        viewModel.openExportDialog()
                    },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Laporan") },
                    label = { Text("Laporan", fontSize = 11.sp, fontWeight = if (selectedNavTab == 1) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = SleekSecondaryContainer,
                        selectedIconColor = SleekOnSecondaryContainer,
                        selectedTextColor = SleekOnSecondaryContainer,
                        unselectedIconColor = SleekTextSecondary,
                        unselectedTextColor = SleekTextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedNavTab == 2,
                    onClick = { selectedNavTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Aturan") },
                    label = { Text("Aturan", fontSize = 11.sp, fontWeight = if (selectedNavTab == 2) FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = SleekSecondaryContainer,
                        selectedIconColor = SleekOnSecondaryContainer,
                        selectedTextColor = SleekOnSecondaryContainer,
                        unselectedIconColor = SleekTextSecondary,
                        unselectedTextColor = SleekTextSecondary
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("home_transactions_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Month Selector Navigation Card
            item {
                MonthSelector(
                    selectedMonthYear = selectedMonthYear,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onSelectMonthYear = { y, m -> viewModel.setMonthYear(y, m) }
                )
            }

            // 2. Sleek Financial Summary Card (Hero 28dp card with Masuk / Keluar glass cards)
            item {
                FinancialSummaryCard(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    transactionCount = monthlyTransactions.size,
                    monthName = monthName
                )
            }

            // 3. Sleek Dual Quick Action Buttons (+ Transaksi & Export Excel)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button 1: + Transaksi (#001D35 Midnight Blue, White Text)
                    Button(
                        onClick = { viewModel.openAddDialog() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("add_transaction_fab"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SleekPrimary,
                            contentColor = SleekOnPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Transaksi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // Button 2: Export Excel (#E8DEF8 Lavender, Dark Text)
                    Button(
                        onClick = { viewModel.openExportDialog() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("export_report_text_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SleekSecondaryContainer,
                            contentColor = SleekOnSecondaryContainer
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Export Excel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // 4. Search & Filter Bar
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_transactions_input"),
                        placeholder = { Text("Cari barang, jenis, atau catatan...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cari",
                                tint = SleekTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Hapus Pencarian")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SleekSurface,
                            focusedContainerColor = SleekSurface,
                            unfocusedBorderColor = SleekBorder.copy(alpha = 0.6f),
                            focusedBorderColor = SleekPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Type Filter Chips (Semua, Uang Masuk, Uang Keluar)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterType == null,
                            onClick = { viewModel.setFilterType(null) },
                            label = { Text("Semua (${monthlyTransactions.size})", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SleekSecondaryContainer,
                                selectedLabelColor = SleekOnSecondaryContainer
                            )
                        )
                        FilterChip(
                            selected = filterType == TransactionType.INCOME,
                            onClick = {
                                viewModel.setFilterType(
                                    if (filterType == TransactionType.INCOME) null else TransactionType.INCOME
                                )
                            },
                            label = { Text("Masuk", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FinancialIncome.copy(alpha = 0.15f),
                                selectedLabelColor = FinancialIncome
                            )
                        )
                        FilterChip(
                            selected = filterType == TransactionType.EXPENSE,
                            onClick = {
                                viewModel.setFilterType(
                                    if (filterType == TransactionType.EXPENSE) null else TransactionType.EXPENSE
                                )
                            },
                            label = { Text("Keluar", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FinancialExpense.copy(alpha = 0.15f),
                                selectedLabelColor = FinancialExpense
                            )
                        )
                    }
                }
            }

            // 5. Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaksi Terakhir",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary
                        )
                    )

                    Text(
                        text = "${filteredTransactions.size} Transaksi",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6750A4)
                        )
                    )
                }
            }

            // 6. Sleek Transaction Table Container Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SleekBorder))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Table Column Header (bg: #F3EDF7, font: 10sp bold uppercase)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SleekSurfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TGL",
                                modifier = Modifier.width(42.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = SleekTextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "JENIS",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = SleekTextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HARGA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = SleekTextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(18.dp))
                            Text(
                                text = "TIPE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = SleekTextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(28.dp))
                        }

                        HorizontalDivider(color = SleekBorder.copy(alpha = 0.6f))

                        // Table Content Rows
                        if (filteredTransactions.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                                    .testTag("empty_transactions_state"),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(SleekSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = SleekTextSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank() || filterType != null) {
                                        "Tidak ada transaksi yang cocok"
                                    } else {
                                        "Belum ada catatan di $monthName"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SleekTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Gunakan tombol + Transaksi untuk menambahkan.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SleekTextSecondary)
                                )

                                if (monthlyTransactions.isEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.populateStarterTransactions() },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("seed_sample_data_button")
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Muat Contoh Data")
                                    }
                                }
                            }
                        } else {
                            filteredTransactions.forEachIndexed { index, item ->
                                TransactionItemCard(
                                    index = index + 1,
                                    item = item,
                                    onEdit = { viewModel.openEditDialog(it) },
                                    onDelete = { viewModel.promptDelete(it) }
                                )
                                if (index < filteredTransactions.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        color = SleekBorder.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Transaction Sheet
    if (isAddEditSheetOpen) {
        AddEditTransactionSheet(
            initialItem = editingTransaction,
            onDismiss = { viewModel.closeAddEditDialog() },
            onSave = { id, dateMillis, category, amount, type, note ->
                viewModel.saveTransaction(id, dateMillis, category, amount, type, note)
            }
        )
    }

    // Export Report Dialog
    if (isExportDialogOpen) {
        ExportReportDialog(
            monthName = monthName,
            transactions = monthlyTransactions,
            onDismiss = { viewModel.closeExportDialog() },
            onShare = { viewModel.shareCurrentMonthReport(context, monthlyTransactions) },
            onSaveToUri = { uri -> viewModel.saveReportToStorage(context, uri, monthlyTransactions) }
        )
    }

    // Delete Confirmation Dialog
    transactionToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDelete() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Hapus Transaksi?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus catatan '${item.category}' sejumlah ${com.example.util.CurrencyFormatter.formatRupiah(item.amount)}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDelete() }) {
                    Text("Batal")
                }
            }
        )
    }
}

