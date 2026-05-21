package com.ade.fuzzyrisk.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesRecordDao {
    @Query("SELECT * FROM sales_records ORDER BY dateMillis DESC")
    fun observeAll(): Flow<List<SalesRecordEntity>>

    @Query("SELECT * FROM sales_records WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<SalesRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SalesRecordEntity): Long

    @Update
    suspend fun update(record: SalesRecordEntity)

    @Delete
    suspend fun delete(record: SalesRecordEntity)

    @Query("DELETE FROM sales_records WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM sales_records")
    suspend fun deleteAll(): Int
}
