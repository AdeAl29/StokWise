package com.ade.fuzzyrisk.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ade.fuzzyrisk.data.SalesRecord
import com.ade.fuzzyrisk.domain.FuzzyTsukamotoCalculator
import java.text.DecimalFormat
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

val ChartBlue = Color(0xFF0284C7)
val Success = Color(0xFF22C55E)
val Warning = Color(0xFFF59E0B)
val Danger = Color(0xFFE11D48)

val NumberFormat = DecimalFormat("0.00")
private val DateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

data class BottomItem(val route: String, val label: String, val icon: ImageVector)

data class PhoneRecordGroup(
    val brand: String,
    val records: List<SalesRecord>
)

data class PhoneTypeGroup(
    val brand: String,
    val models: List<String>
)

data class PhoneCatalogGroup(
    val brand: String,
    val modelsByYear: Map<String, List<String>>
)

data class PhoneModelOption(
    val year: String,
    val name: String
)

private val IgnoredPhonePrefixes = setOf("hp", "handphone", "smartphone", "ponsel")

private val PhoneBrandLabels = mapOf(
    "advan" to "Advan",
    "apple" to "Apple",
    "asus" to "Asus",
    "evercoss" to "Evercoss",
    "google" to "Google",
    "honor" to "Honor",
    "huawei" to "Huawei",
    "infinix" to "Infinix",
    "iphone" to "iPhone",
    "iqoo" to "iQOO",
    "itel" to "itel",
    "lava" to "Lava",
    "lenovo" to "Lenovo",
    "meizu" to "Meizu",
    "motorola" to "Motorola",
    "nothing" to "Nothing",
    "nokia" to "Nokia",
    "nubia" to "nubia",
    "oneplus" to "OnePlus",
    "oppo" to "OPPO",
    "poco" to "Poco",
    "realme" to "realme",
    "redmi" to "Redmi",
    "samsung" to "Samsung",
    "sharp" to "Sharp",
    "sony" to "Sony",
    "tecno" to "TECNO",
    "vivo" to "vivo",
    "wiko" to "Wiko",
    "zte" to "ZTE",
    "xiaomi" to "Xiaomi"
)

const val OtherPhoneBrand = "Lainnya"

private val PhoneImageSearchAliases = mapOf(
    "oppo a6x" to "OPPO A6X",
    "realme 60x" to "realme Note 60X",
    "realme note 60x" to "realme Note 60X",
    "redmi note 15c" to "Redmi Note 15C",
    "samsung a26" to "Samsung Galaxy A26 5G",
    "samsung galaxy a26 5g" to "Samsung Galaxy A26 5G",
    "infinix smart 20" to "Infinix Smart 20"
)

val PhoneCatalog = listOf(
    PhoneCatalogGroup(
        brand = "OPPO",
        modelsByYear = mapOf(
            "2025" to listOf(
                "OPPO Reno13 5G",
                "Reno13 F 5G",
                "Reno13 F 4G",
                "A5 Pro",
                "A5 Pro 5G",
                "Find N5",
                "A5i",
                "A5",
                "A5x",
                "A5i Pro",
                "A5i Pro 5G",
                "Reno14 F 5G",
                "Reno14 5G",
                "Reno14 Pro 5G",
                "Find X9",
                "Find X9 Pro",
                "A6 Pro / A6 Pro 5G"
            ),
            "2026" to listOf(
                "OPPO A6c",
                "A6X",
                "Find X9s",
                "Find X9 Ultra",
                "Reno15 5G",
                "Reno15 Pro Max 5G"
            )
        )
    ),
    PhoneCatalogGroup(
        brand = "vivo",
        modelsByYear = mapOf(
            "2025" to listOf(
                "vivo X200",
                "X200 Pro",
                "Y29",
                "V50",
                "V50 Lite 4G",
                "V50 Lite 5G",
                "Y19sGT 5G",
                "V60",
                "X Fold5",
                "V60 Lite 4G",
                "V60 Lite 5G",
                "Y21d",
                "X300",
                "X300 Pro"
            ),
            "2026" to listOf(
                "vivo Y05",
                "V70 FE",
                "V70",
                "Y31d Pro",
                "Y11d",
                "X300 Ultra"
            )
        )
    ),
    PhoneCatalogGroup(
        brand = "Redmi",
        modelsByYear = mapOf(
            "2025" to listOf(
                "Redmi A5",
                "Redmi 13x",
                "Redmi 15C",
                "Redmi Note 14 4G",
                "Redmi Note 14 5G",
                "Redmi Note 14 Pro 5G",
                "Redmi Note 14 Pro+ 5G"
            ),
            "2026" to listOf(
                "Redmi A7 Pro",
                "Redmi Note 15 4G",
                "Redmi Note 15 5G",
                "Redmi Note 15C",
                "Redmi Note 15 Pro 5G",
                "Redmi Note 15 Pro+ 5G"
            )
        )
    ),
    PhoneCatalogGroup(
        brand = "Infinix",
        modelsByYear = mapOf(
            "2025" to listOf(
                "Infinix Smart 9 HD",
                "Note 50",
                "Note 50 Pro",
                "GT 30 Pro",
                "GT 30",
                "Smart 10",
                "Smart 10 Plus",
                "Hot 60i",
                "Hot 60 Pro"
            ),
            "2026" to listOf(
                "Infinix Smart 20",
                "Note Edge",
                "Note 60",
                "Note 60 Pro",
                "GT 50 Pro",
                "Hot 70"
            )
        )
    ),
    PhoneCatalogGroup(
        brand = "Samsung",
        modelsByYear = mapOf(
            "2025" to listOf(
                "Galaxy S25",
                "S25+",
                "S25 Ultra",
                "S25 Edge",
                "S25 FE",
                "A26",
                "Galaxy A26 5G",
                "A36 5G",
                "A56 5G",
                "A07",
                "A17",
                "A17 5G",
                "Galaxy Z Flip7",
                "Z Flip7 FE",
                "Z Fold7",
                "XCover7 Pro"
            ),
            "2026" to listOf(
                "Galaxy A07 5G",
                "Galaxy S26",
                "S26+",
                "S26 Ultra",
                "Galaxy A37",
                "Galaxy A57"
            )
        )
    ),
    PhoneCatalogGroup(
        brand = "realme",
        modelsByYear = mapOf(
            "2025" to listOf(
                "realme Note 60X",
                "60X",
                "C71",
                "P3 5G",
                "14 5G",
                "14T 5G",
                "GT 7T",
                "GT 7",
                "GT 7 Dream Edition",
                "15T 5G"
            ),
            "2026" to listOf(
                "realme Note 80",
                "C85 5G",
                "16 5G",
                "16 Pro 5G",
                "16 Pro+ 5G",
                "C100i",
                "C100x",
                "C100"
            )
        )
    ),
    PhoneCatalogGroup(
        brand = "nubia",
        modelsByYear = mapOf(
            "2025" to listOf(
                "nubia V70 Max",
                "nubia Focus 2 5G",
                "nubia Neo 3 5G",
                "nubia Neo 3 GT 5G"
            ),
            "2026" to listOf(
                "nubia V80 Max",
                "nubia A76 5G",
                "nubia REDMAGIC 11 Pro",
                "nubia Neo 5 5G",
                "nubia Neo 5 GT 5G"
            )
        )
    ),
    PhoneCatalogGroup(
        brand = "itel",
        modelsByYear = mapOf(
            "2025" to listOf(
                "itel Power 70 / P70",
                "itel A90",
                "itel City 100",
                "itel Zeno 20",
                "itel S26 Ultra / Super 26 Ultra"
            ),
            "2026" to listOf(
                "itel City 200",
                "itel A100CS",
                "itel A100C",
                "itel A200"
            )
        )
    ),
    PhoneCatalogGroup(
        brand = "TECNO",
        modelsByYear = mapOf(
            "2025" to listOf(
                "TECNO Camon 40",
                "Camon 40 Pro 5G",
                "POVA 7",
                "POVA 7 Ultra 5G",
                "Spark Go 2",
                "Spark 40",
                "Spark 40 Pro",
                "Spark 40 Pro+"
            ),
            "2026" to listOf(
                "TECNO Spark Go 3",
                "Camon 50",
                "Camon 50 Pro 5G",
                "Spark 50 4G"
            )
        )
    )
)

val IndonesianPhoneBrands = PhoneCatalog.map { it.brand }

fun phoneModelsForBrand(brand: String): List<PhoneModelOption> {
    return PhoneCatalog
        .firstOrNull { it.brand.equals(brand.trim(), ignoreCase = true) }
        ?.modelsByYear
        ?.flatMap { (year, models) -> models.map { model -> PhoneModelOption(year, model) } }
        .orEmpty()
}

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

fun buildPhoneType(brand: String, model: String): String {
    val normalizedBrand = normalizedPhoneType(brand)
    val normalizedModel = normalizedPhoneType(model)
    if (normalizedBrand.isBlank()) return normalizedModel
    if (normalizedBrand.equals(OtherPhoneBrand, ignoreCase = true)) return normalizedModel
    val brandLower = normalizedBrand.lowercase(Locale.getDefault())
    val modelLower = normalizedModel.lowercase(Locale.getDefault())
    if (modelLower == brandLower || modelLower.startsWith("$brandLower ")) return normalizedModel
    return listOf(normalizedBrand, normalizedModel)
        .filter { it.isNotBlank() }
        .joinToString(" ")
}

fun phoneImageUrl(phoneType: String): String? {
    val cleaned = normalizedPhoneType(phoneType)
    if (cleaned.isBlank()) return null
    val searchName = PhoneImageSearchAliases[cleaned.lowercase(Locale.getDefault())] ?: cleaned
    val query = URLEncoder.encode("$searchName official phone product image", StandardCharsets.UTF_8.name())
    return "https://tse1.mm.bing.net/th?q=$query&w=160&h=160&c=7&rs=1&p=0&o=5&pid=1.7"
}

fun phoneBrandName(phoneType: String): String {
    val token = normalizedPhoneType(phoneType)
        .split(" ")
        .firstOrNull { normalizePhoneToken(it) !in IgnoredPhonePrefixes }
        ?: return "Lainnya"
    val normalized = normalizePhoneToken(token)
    if (normalized.isBlank()) return "Lainnya"
    return PhoneBrandLabels[normalized] ?: normalized.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
    }
}

fun phoneModelName(phoneType: String, brand: String = phoneBrandName(phoneType)): String {
    val cleaned = normalizedPhoneType(phoneType)
    if (cleaned.isBlank()) return "-"

    val parts = cleaned.split(" ").filter { it.isNotBlank() }
    val brandIndex = parts.indexOfFirst { normalizePhoneToken(it) !in IgnoredPhonePrefixes }
    if (brandIndex == -1) return cleaned

    val brandToken = parts[brandIndex]
    val modelParts = if (phoneBrandName(brandToken).equals(brand, ignoreCase = true)) {
        parts.drop(brandIndex + 1)
    } else {
        parts.drop(brandIndex)
    }
    return modelParts.joinToString(" ").ifBlank { cleaned }
}

fun phoneRecordGroups(records: List<SalesRecord>): List<PhoneRecordGroup> {
    return records
        .groupBy { phoneBrandName(it.phoneType) }
        .map { (brand, groupedRecords) ->
            PhoneRecordGroup(brand = brand, records = groupedRecords)
        }
        .sortedBy { it.brand.lowercase(Locale.getDefault()) }
}

fun phoneTypeGroupsForRisk(records: List<SalesRecord>, level: String): List<PhoneTypeGroup> {
    return records
        .filter { it.riskLevel == level && it.phoneType.isNotBlank() }
        .groupBy { phoneBrandName(it.phoneType) }
        .map { (brand, groupedRecords) ->
            val models = groupedRecords
                .map { phoneModelName(it.phoneType, brand) }
                .filter { it.isNotBlank() }
                .distinctBy { it.lowercase(Locale.getDefault()) }
                .take(6)
            PhoneTypeGroup(brand = brand, models = models)
        }
        .filter { it.models.isNotEmpty() }
        .sortedBy { it.brand.lowercase(Locale.getDefault()) }
}

fun phoneTypesForRisk(records: List<SalesRecord>, level: String): List<String> {
    return phoneTypeGroupsForRisk(records, level)
        .flatMap { group -> group.models.map { model -> "${group.brand} $model" } }
        .take(6)
}

fun percent(count: Int, total: Int): Int {
    if (total == 0) return 0
    return ((count.toDouble() / total.toDouble()) * 100.0).roundToInt()
}

private fun normalizedPhoneType(phoneType: String): String {
    return phoneType.trim().replace(Regex("\\s+"), " ")
}

private fun normalizePhoneToken(token: String): String {
    return token
        .trim()
        .trim('-', '_', ',', '.', ':', ';', '/', '\\')
        .lowercase(Locale.getDefault())
}
