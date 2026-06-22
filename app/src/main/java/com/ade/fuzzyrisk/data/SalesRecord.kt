package com.ade.fuzzyrisk.data

data class SalesRecord(
    val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val phoneType: String,
    val sales: Int,
    val incomingStock: Int = 0,
    val stock: Int,
    val demand: Int,
    val riskLevel: String,
    val zValue: Double,
    val fuzzification: String,
    val inference: String,
    val defuzzification: String
)
