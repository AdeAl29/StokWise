package com.ade.fuzzyrisk.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ade.fuzzyrisk.data.SalesRecord
import com.ade.fuzzyrisk.domain.FuzzyTsukamotoCalculator
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

val ChartBlue = Color(0xFF0284C7)
val Success = Color(0xFF22C55E)
val Warning = Color(0xFFF59E0B)
val Danger = Color(0xFFE11D48)

val NumberFormat = DecimalFormat("0.00")
private val DateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

data class BottomItem(val route: String, val label: String, val icon: ImageVector)

fun SalesRecord.dateText(): String {
    return Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).format(DateFormatter)
}

fun riskColor(level: String): Color {
    return when (level) {
        "Rendah" -> Success
        "Tinggi" -> Danger
        "Sedang" -> Warning
        else -> ChartBlue
    }
}

fun riskFromZ(z: Double): String {
    return FuzzyTsukamotoCalculator.riskLevelFromZ(z).label
}

fun phoneTypesForRisk(records: List<SalesRecord>, level: String): List<String> {
    return records
        .filter { it.riskLevel == level }
        .map { it.phoneType.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .take(6)
}

fun percent(count: Int, total: Int): Int {
    if (total == 0) return 0
    return ((count.toDouble() / total.toDouble()) * 100.0).roundToInt()
}
