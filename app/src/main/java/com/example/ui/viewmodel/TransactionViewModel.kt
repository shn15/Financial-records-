package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.data.repository.TransactionRepository
import com.example.util.CsvExporter
import com.example.util.CurrencyFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class MonthYear(val year: Int, val month: Int)

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TransactionRepository(db.transactionDao())
    }

    private val calendar = Calendar.getInstance()
    private val _selectedMonthYear = MutableStateFlow(
        MonthYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
    )
    val selectedMonthYear: StateFlow<MonthYear> = _selectedMonthYear.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow<TransactionType?>(null) // null = Semua
    val filterType: StateFlow<TransactionType?> = _filterType.asStateFlow()

    private val _filterCategory = MutableStateFlow<String?>(null) // null = Semua Kategori
    val filterCategory: StateFlow<String?> = _filterCategory.asStateFlow()

    // Monthly raw transactions from database
    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyTransactions: StateFlow<List<TransactionItem>> = _selectedMonthYear.flatMapLatest { my ->
        val range = CurrencyFormatter.getMonthRangeMillis(my.year, my.month)
        repository.getTransactionsByMonth(range.first, range.second)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered list based on search, type, and category
    val filteredTransactions: StateFlow<List<TransactionItem>> = combine(
        monthlyTransactions,
        _searchQuery,
        _filterType,
        _filterCategory
    ) { list, query, type, cat ->
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.category.contains(query, ignoreCase = true) ||
                    item.note.contains(query, ignoreCase = true) ||
                    item.amount.toString().contains(query)
            val matchesType = type == null || item.type == type
            val matchesCategory = cat == null || item.category.equals(cat, ignoreCase = true)
            matchesQuery && matchesType && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI Dialog States
    private val _isAddEditSheetOpen = MutableStateFlow(false)
    val isAddEditSheetOpen: StateFlow<Boolean> = _isAddEditSheetOpen.asStateFlow()

    private val _editingTransaction = MutableStateFlow<TransactionItem?>(null)
    val editingTransaction: StateFlow<TransactionItem?> = _editingTransaction.asStateFlow()

    private val _isExportDialogOpen = MutableStateFlow(false)
    val isExportDialogOpen: StateFlow<Boolean> = _isExportDialogOpen.asStateFlow()

    private val _transactionToDelete = MutableStateFlow<TransactionItem?>(null)
    val transactionToDelete: StateFlow<TransactionItem?> = _transactionToDelete.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    init {
        checkAndSeedSampleData()
    }

    private fun checkAndSeedSampleData() {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication())
            // We can pre-populate initial helpful transactions if DB is completely empty
            // so the user can immediately experience the monthly balance and Excel export
            kotlinx.coroutines.delay(200)
            val cal = Calendar.getInstance()
            val now = cal.timeInMillis
            val currentYear = cal.get(Calendar.YEAR)
            val currentMonth = cal.get(Calendar.MONTH)
            val range = CurrencyFormatter.getMonthRangeMillis(currentYear, currentMonth)
            
            // Check if any data exists in this month
            // If empty, let's add a few helpful starter entries
            // (e.g. Saldo Awal / Gaji, Belanja Bulanan, Makanan)
        }
    }

    fun populateStarterTransactions() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val now = cal.timeInMillis
            val starterItems = listOf(
                TransactionItem(
                    dateMillis = now - (3 * 24 * 60 * 60 * 1000L),
                    category = "Gaji & Pemasukan",
                    amount = 6500000.0,
                    type = TransactionType.INCOME,
                    note = "Gaji Pokok & Bonus Bulanan"
                ),
                TransactionItem(
                    dateMillis = now - (2 * 24 * 60 * 60 * 1000L),
                    category = "Belanja Kebutuhan",
                    amount = 450000.0,
                    type = TransactionType.EXPENSE,
                    note = "Belanja sembako & perlengkapan rumah"
                ),
                TransactionItem(
                    dateMillis = now - (1 * 24 * 60 * 60 * 1000L),
                    category = "Tagihan & Utilitas",
                    amount = 250000.0,
                    type = TransactionType.EXPENSE,
                    note = "Tagihan Listrik PLN & Internet Wi-Fi"
                ),
                TransactionItem(
                    dateMillis = now - (10 * 60 * 60 * 1000L),
                    category = "Makanan & Minuman",
                    amount = 45000.0,
                    type = TransactionType.EXPENSE,
                    note = "Makan siang dan minuman"
                ),
                TransactionItem(
                    dateMillis = now,
                    category = "Penjualan / Usaha",
                    amount = 350000.0,
                    type = TransactionType.INCOME,
                    note = "Hasil jualan produk sampingan"
                )
            )
            repository.insertAll(starterItems)
            _feedbackMessage.value = "Data contoh berhasil ditambahkan!"
        }
    }

    fun previousMonth() {
        val current = _selectedMonthYear.value
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, current.year)
        cal.set(Calendar.MONTH, current.month)
        cal.add(Calendar.MONTH, -1)
        _selectedMonthYear.value = MonthYear(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
    }

    fun nextMonth() {
        val current = _selectedMonthYear.value
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, current.year)
        cal.set(Calendar.MONTH, current.month)
        cal.add(Calendar.MONTH, 1)
        _selectedMonthYear.value = MonthYear(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
    }

    fun setMonthYear(year: Int, month: Int) {
        _selectedMonthYear.value = MonthYear(year, month)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: TransactionType?) {
        _filterType.value = type
    }

    fun setFilterCategory(category: String?) {
        _filterCategory.value = category
    }

    fun openAddDialog(defaultType: TransactionType = TransactionType.EXPENSE) {
        _editingTransaction.value = null
        _isAddEditSheetOpen.value = true
    }

    fun openEditDialog(transaction: TransactionItem) {
        _editingTransaction.value = transaction
        _isAddEditSheetOpen.value = true
    }

    fun closeAddEditDialog() {
        _isAddEditSheetOpen.value = false
        _editingTransaction.value = null
    }

    fun openExportDialog() {
        _isExportDialogOpen.value = true
    }

    fun closeExportDialog() {
        _isExportDialogOpen.value = false
    }

    fun promptDelete(transaction: TransactionItem) {
        _transactionToDelete.value = transaction
    }

    fun dismissDelete() {
        _transactionToDelete.value = null
    }

    fun confirmDelete() {
        val item = _transactionToDelete.value ?: return
        viewModelScope.launch {
            repository.delete(item)
            _transactionToDelete.value = null
            _feedbackMessage.value = "Transaksi berhasil dihapus"
        }
    }

    fun saveTransaction(
        id: Long = 0,
        dateMillis: Long,
        category: String,
        amount: Double,
        type: TransactionType,
        note: String
    ) {
        viewModelScope.launch {
            val item = TransactionItem(
                id = id,
                dateMillis = dateMillis,
                category = category.trim().ifBlank { if (type == TransactionType.INCOME) "Pemasukan Lainnya" else "Pengeluaran Lainnya" },
                amount = amount,
                type = type,
                note = note.trim()
            )
            if (id == 0L) {
                repository.insert(item)
                _feedbackMessage.value = "Transaksi berhasil dicatat"
            } else {
                repository.update(item)
                _feedbackMessage.value = "Transaksi berhasil diperbarui"
            }
            closeAddEditDialog()
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun getMonthName(): String {
        val my = _selectedMonthYear.value
        return CurrencyFormatter.formatMonthYear(my.year, my.month)
    }

    fun calculateIncome(list: List<TransactionItem>): Double {
        return list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }

    fun calculateExpense(list: List<TransactionItem>): Double {
        return list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    fun generateCsvReportContent(list: List<TransactionItem>): String {
        val income = calculateIncome(list)
        val expense = calculateExpense(list)
        val balance = income - expense
        return CsvExporter.generateCsvContent(
            monthName = getMonthName(),
            transactions = list,
            totalIncome = income,
            totalExpense = expense,
            netBalance = balance
        )
    }

    fun shareCurrentMonthReport(context: Context, list: List<TransactionItem>) {
        val content = generateCsvReportContent(list)
        val success = CsvExporter.shareCsvReport(context, getMonthName(), content)
        if (success) {
            _feedbackMessage.value = "Membuka dialog berbagi laporan..."
        } else {
            _feedbackMessage.value = "Gagal membagikan laporan"
        }
    }

    fun saveReportToStorage(context: Context, uri: Uri, list: List<TransactionItem>) {
        val content = generateCsvReportContent(list)
        val success = CsvExporter.saveCsvToUri(context, uri, content)
        if (success) {
            _feedbackMessage.value = "Laporan berhasil disimpan ke perangkat!"
            closeExportDialog()
        } else {
            _feedbackMessage.value = "Gagal menyimpan laporan"
        }
    }
}
