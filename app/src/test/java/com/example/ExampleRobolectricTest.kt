package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.util.CsvExporter
import com.example.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Buku Kas", appName)
    }

    @Test
    fun `format rupiah correctly`() {
        val formatted = CurrencyFormatter.formatRupiah(50000.0)
        assertTrue(formatted.contains("50.000"))
    }

    @Test
    fun `generate csv report includes required columns and totals`() {
        val sampleList = listOf(
            TransactionItem(
                id = 1,
                dateMillis = System.currentTimeMillis(),
                category = "Makanan & Minuman",
                amount = 25000.0,
                type = TransactionType.EXPENSE,
                note = "Makan Siang"
            ),
            TransactionItem(
                id = 2,
                dateMillis = System.currentTimeMillis(),
                category = "Gaji",
                amount = 5000000.0,
                type = TransactionType.INCOME,
                note = "Gaji Bulanan"
            )
        )
        val csv = CsvExporter.generateCsvContent(
            monthName = "Agustus 2026",
            transactions = sampleList,
            totalIncome = 5000000.0,
            totalExpense = 25000.0,
            netBalance = 4975000.0
        )
        assertTrue(csv.contains("LAPORAN KEUANGAN BULANAN"))
        assertTrue(csv.contains("No,Tanggal,Jenis,Harga Barang,Jenis Transaksi,Keterangan"))
        assertTrue(csv.contains("Makanan & Minuman"))
        assertTrue(csv.contains("Gaji"))
        assertTrue(csv.contains("Total Uang Masuk"))
        assertTrue(csv.contains("Total Uang Keluar"))
        assertTrue(csv.contains("Sisa Saldo"))
        assertTrue(csv.contains("Total Semua Nominal Transaksi"))
    }
}

