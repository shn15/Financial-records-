package com.example.data.repository

import com.example.data.local.TransactionDao
import com.example.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<TransactionItem>> = transactionDao.getAllTransactions()

    fun getTransactionsByMonth(startMillis: Long, endMillis: Long): Flow<List<TransactionItem>> {
        return transactionDao.getTransactionsByDateRange(startMillis, endMillis)
    }

    suspend fun getById(id: Long): TransactionItem? = transactionDao.getTransactionById(id)

    suspend fun insert(transaction: TransactionItem): Long = transactionDao.insert(transaction)

    suspend fun insertAll(transactions: List<TransactionItem>) = transactionDao.insertAll(transactions)

    suspend fun update(transaction: TransactionItem) = transactionDao.update(transaction)

    suspend fun delete(transaction: TransactionItem) = transactionDao.delete(transaction)

    suspend fun deleteById(id: Long) = transactionDao.deleteById(id)

    suspend fun clearAll() = transactionDao.clearAll()
}
