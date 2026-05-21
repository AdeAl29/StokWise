package com.ade.fuzzyrisk.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_records")
data class SalesRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateMillis: Long,
    val phoneType: String,
    val sales: Int,
    val stock: Int,
    val demand: Int,
    val riskLevel: String,
    val zValue: Double,
    val fuzzification: String,
    val inference: String,
    val defuzzification: String
)
