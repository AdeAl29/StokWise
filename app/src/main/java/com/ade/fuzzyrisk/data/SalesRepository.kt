package com.ade.fuzzyrisk.data

import android.content.Context
import com.ade.fuzzyrisk.data.local.FuzzyRiskDatabase
import com.ade.fuzzyrisk.data.local.toEntity
import com.ade.fuzzyrisk.data.local.toSalesRecord
import com.ade.fuzzyrisk.data.local.toSalesRecords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SalesRepository(context: Context) {
    private val dao = FuzzyRiskDatabase.getInstance(context).salesRecordDao()

    val records: Flow<List<SalesRecord>> = dao.observeAll()
        .map { entities -> entities.toSalesRecords() }

    fun observeById(id: Long): Flow<SalesRecord?> {
        return dao.observeById(id).map { entity -> entity?.toSalesRecord() }
    }

    suspend fun insert(record: SalesRecord): Long = withContext(Dispatchers.IO) {
        dao.insert(record.toEntity())
    }

    suspend fun update(record: SalesRecord) = withContext(Dispatchers.IO) {
        dao.update(record.toEntity())
    }

    suspend fun delete(record: SalesRecord) = withContext(Dispatchers.IO) {
        dao.delete(record.toEntity())
    }

    suspend fun deleteById(id: Long): Int = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun deleteAll(): Int = withContext(Dispatchers.IO) {
        dao.deleteAll()
    }
}
