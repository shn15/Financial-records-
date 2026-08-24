package com.example.data.model

enum class TransactionType(val label: String, val exportLabel: String) {
    INCOME("Uang Masuk", "Masuk"),
    EXPENSE("Uang Keluar", "Keluar");

    companion object {
        fun fromString(value: String): TransactionType {
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || 
                it.label.equals(value, ignoreCase = true) ||
                it.exportLabel.equals(value, ignoreCase = true)
            } ?: EXPENSE
        }
    }
}
