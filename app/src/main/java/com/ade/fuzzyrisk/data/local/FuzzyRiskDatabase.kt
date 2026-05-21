package com.ade.fuzzyrisk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [SalesRecordEntity::class],
    version = 3,
    exportSchema = false
)
abstract class FuzzyRiskDatabase : RoomDatabase() {
    abstract fun salesRecordDao(): SalesRecordDao

    companion object {
        private const val DB_NAME = "fuzzyrisk.db"

        @Volatile
        private var INSTANCE: FuzzyRiskDatabase? = null

        fun getInstance(context: Context): FuzzyRiskDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FuzzyRiskDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE sales_records ADD COLUMN phoneType TEXT NOT NULL DEFAULT 'Handphone'"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                normalizeSalesRecordsTable(db)
            }
        }

        private fun normalizeSalesRecordsTable(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS sales_records_room (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    dateMillis INTEGER NOT NULL,
                    phoneType TEXT NOT NULL,
                    sales INTEGER NOT NULL,
                    stock INTEGER NOT NULL,
                    demand INTEGER NOT NULL,
                    riskLevel TEXT NOT NULL,
                    zValue REAL NOT NULL,
                    fuzzification TEXT NOT NULL,
                    inference TEXT NOT NULL,
                    defuzzification TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO sales_records_room (
                    id,
                    dateMillis,
                    phoneType,
                    sales,
                    stock,
                    demand,
                    riskLevel,
                    zValue,
                    fuzzification,
                    inference,
                    defuzzification
                )
                SELECT
                    id,
                    dateMillis,
                    phoneType,
                    sales,
                    stock,
                    demand,
                    riskLevel,
                    zValue,
                    fuzzification,
                    inference,
                    defuzzification
                FROM sales_records
                """.trimIndent()
            )
            db.execSQL("DROP TABLE sales_records")
            db.execSQL("ALTER TABLE sales_records_room RENAME TO sales_records")
        }
    }
}
