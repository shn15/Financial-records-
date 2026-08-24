package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateMillis: Long,
    val category: String, // Jenis barang / kategori (e.g. Makanan, Gaji, Belanja)
    val amount: Double,   // Harga barang / nominal
    val type: TransactionType, // Jenis transaksi (Masuk / Keluar)
    val note: String = "", // Keterangan
    val createdAt: Long = System.currentTimeMillis()
)
