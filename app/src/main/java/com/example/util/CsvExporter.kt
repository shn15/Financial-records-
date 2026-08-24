package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object CsvExporter {

    private fun escapeCsv(text: String): String {
        var result = text.replace("\"", "\"\"")
        if (result.contains(",") || result.contains("\n") || result.contains("\"") || result.contains(";")) {
            result = "\"$result\""
        }
        return result
    }

    fun generateCsvContent(
        monthName: String,
        transactions: List<TransactionItem>,
        totalIncome: Double,
        totalExpense: Double,
        netBalance: Double
    ): String {
        val sb = StringBuilder()
        // Add UTF-8 Byte Order Mark (BOM) so Excel directly recognizes UTF-8 encoding
        sb.append("\uFEFF")

        // 1. JUDUL & INFORMASI LAPORAN
        sb.append("LAPORAN KEUANGAN BULANAN\n")
        sb.append("Periode:,").append(escapeCsv(monthName)).append("\n")
        sb.append("Waktu Dibuat:,").append(escapeCsv(CurrencyFormatter.formatDate(System.currentTimeMillis()))).append("\n\n")

        // 2. RINGKASAN SALDO
        sb.append("RINGKASAN SALDO PER BULAN\n")
        sb.append("Total Uang Masuk / Pemasukan,").append(CurrencyFormatter.formatRupiah(totalIncome)).append("\n")
        sb.append("Total Uang Keluar / Pengeluaran,").append(CurrencyFormatter.formatRupiah(totalExpense)).append("\n")
        sb.append("Sisa Saldo,").append(CurrencyFormatter.formatRupiah(netBalance)).append("\n\n")

        // 3. TABEL CATATAN TRANSAKSI
        sb.append("CATATAN TRANSAKSI\n")
        sb.append("No,Tanggal,Jenis,Harga Barang,Jenis Transaksi,Keterangan\n")

        // Sort ascending by date for the report table
        val sortedList = transactions.sortedBy { it.dateMillis }
        var totalNominalSum = 0.0

        sortedList.forEachIndexed { index, item ->
            totalNominalSum += item.amount
            val no = (index + 1).toString()
            val tanggal = escapeCsv(CurrencyFormatter.formatDate(item.dateMillis))
            val jenis = escapeCsv(item.category)
            val hargaBarang = item.amount.toLong().toString()
            val jenisTransaksi = escapeCsv(item.type.exportLabel)
            val keterangan = escapeCsv(item.note.ifBlank { "-" })

            sb.append("$no,$tanggal,$jenis,$hargaBarang,$jenisTransaksi,$keterangan\n")
        }

        // 4. TOTAL REKAPITULASI OTOMATIS OLEH SISTEM
        sb.append("\nREKAPITULASI JUMLAH (OTOMATIS)\n")
        sb.append("Total Transaksi,").append(transactions.size).append(" transaksi\n")
        sb.append("Total Nominal Uang Masuk,").append(CurrencyFormatter.formatRupiah(totalIncome)).append("\n")
        sb.append("Total Nominal Uang Keluar,").append(CurrencyFormatter.formatRupiah(totalExpense)).append("\n")
        sb.append("Total Semua Nominal Transaksi,").append(CurrencyFormatter.formatRupiah(totalNominalSum)).append("\n")
        sb.append("Sisa Saldo Akhir,").append(CurrencyFormatter.formatRupiah(netBalance)).append("\n")

        return sb.toString()
    }

    fun shareCsvReport(context: Context, monthName: String, csvContent: String): Boolean {
        return try {
            val reportDir = File(context.cacheDir, "reports")
            if (!reportDir.exists()) {
                reportDir.mkdirs()
            }

            val sanitizedMonth = monthName.replace(" ", "_").replace("/", "_")
            val fileName = "Laporan_Keuangan_${sanitizedMonth}.csv"
            val file = File(reportDir, fileName)

            val fos = FileOutputStream(file)
            val writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)
            writer.write(csvContent)
            writer.flush()
            writer.close()
            fos.close()

            val authority = "${context.packageName}.fileprovider"
            val fileUri: Uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan $monthName")
                putExtra(Intent.EXTRA_TEXT, "Berikut terlampir Laporan Keuangan Bulanan ($monthName) dalam format CSV/Excel.")
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Bagikan Laporan Excel / CSV")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveCsvToUri(context: Context, uri: Uri, csvContent: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    writer.write(csvContent)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
