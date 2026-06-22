package com.ade.fuzzyrisk.data.local

import com.ade.fuzzyrisk.data.SalesRecord

fun SalesRecordEntity.toSalesRecord(): SalesRecord {
    return SalesRecord(
        id = id,
        dateMillis = dateMillis,
        phoneType = phoneType,
        sales = sales,
        incomingStock = incomingStock,
        stock = stock,
        demand = demand,
        riskLevel = riskLevel,
        zValue = zValue,
        fuzzification = fuzzification,
        inference = inference,
        defuzzification = defuzzification
    )
}

fun SalesRecord.toEntity(): SalesRecordEntity {
    return SalesRecordEntity(
        id = id,
        dateMillis = dateMillis,
        phoneType = phoneType,
        sales = sales,
        incomingStock = incomingStock,
        stock = stock,
        demand = demand,
        riskLevel = riskLevel,
        zValue = zValue,
        fuzzification = fuzzification,
        inference = inference,
        defuzzification = defuzzification
    )
}

fun List<SalesRecordEntity>.toSalesRecords(): List<SalesRecord> {
    return map { it.toSalesRecord() }
}
