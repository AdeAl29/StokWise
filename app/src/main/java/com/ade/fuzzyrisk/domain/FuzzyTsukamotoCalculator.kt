package com.ade.fuzzyrisk.domain

import java.util.Locale
import kotlin.math.max
import kotlin.math.min

enum class RiskLevel(val label: String) {
    LOW("Rendah"),
    MEDIUM("Sedang"),
    HIGH("Tinggi")
}

data class FuzzyResult(
    val riskLevel: RiskLevel,
    val zValue: Double,
    val fuzzification: String,
    val inference: String,
    val defuzzification: String
)

data class FuzzyGraphSpec(
    val title: String,
    val currentValue: Double,
    val currentLabel: String,
    val xMin: Double,
    val xMax: Double,
    val xTicks: List<Double>,
    val lines: List<FuzzyGraphLine>
)

data class FuzzyGraphLine(
    val label: String,
    val points: List<FuzzyGraphPoint>
)

data class FuzzyGraphPoint(
    val x: Double,
    val y: Double
)

object FuzzyTsukamotoCalculator {
    fun calculate(sales: Int, stock: Int, demand: Int): FuzzyResult {
        require(sales >= 0) { "Jumlah penjualan tidak boleh negatif." }
        require(stock >= 0) { "Jumlah stok tidak boleh negatif." }
        require(demand >= 0) { "Jumlah permintaan tidak boleh negatif." }

        val salesSets = memberships(sales.toDouble(), SALES_DOMAIN)
        val stockSets = memberships(stock.toDouble(), STOCK_DOMAIN)
        val demandSets = memberships(demand.toDouble(), DEMAND_DOMAIN)
        val rules = mutableListOf<RuleResult>()

        for ((salesName, salesMu) in salesSets) {
            for ((stockName, stockMu) in stockSets) {
                for ((demandName, demandMu) in demandSets) {
                    val alpha = min(salesMu, min(stockMu, demandMu))
                    if (alpha > 0.0) {
                        val risk = inferRisk(salesName, stockName, demandName)
                        rules += RuleResult(salesName, stockName, demandName, risk, alpha, crispZ(risk, alpha))
                    }
                }
            }
        }

        val sumAlpha = rules.sumOf { it.alpha }
        val sumWeightedZ = rules.sumOf { it.alpha * it.z }
        val z = if (sumAlpha == 0.0) DEFAULT_RISK_Z else sumWeightedZ / sumAlpha
        val level = riskLevelFromZ(z)

        return FuzzyResult(
            riskLevel = level,
            zValue = z,
            fuzzification = buildFuzzification(salesSets, stockSets, demandSets, sales, stock, demand),
            inference = buildInference(rules),
            defuzzification = buildDefuzzification(rules, sumWeightedZ, sumAlpha, z, level)
        )
    }

    fun riskLevelFromZ(z: Double): RiskLevel {
        return when {
            z < LOW_RISK_LIMIT -> RiskLevel.LOW
            z <= MEDIUM_RISK_LIMIT -> RiskLevel.MEDIUM
            else -> RiskLevel.HIGH
        }
    }

    fun inputGraphSpecs(sales: Int, stock: Int, demand: Int): List<FuzzyGraphSpec> {
        return listOf(
            inputGraphSpec("Grafik Keanggotaan Penjualan", sales.toDouble(), "Penjualan", SALES_DOMAIN),
            inputGraphSpec("Grafik Keanggotaan Stok", stock.toDouble(), "Stok", STOCK_DOMAIN),
            inputGraphSpec("Grafik Keanggotaan Permintaan", demand.toDouble(), "Permintaan", DEMAND_DOMAIN)
        )
    }

    fun outputGraphSpec(zValue: Double): FuzzyGraphSpec {
        return FuzzyGraphSpec(
            title = "Grafik Keanggotaan Risiko",
            currentValue = zValue,
            currentLabel = "Z",
            xMin = 0.0,
            xMax = RISK_MAX,
            xTicks = listOf(0.0, MEDIUM_RISK_MIN, LOW_RISK_LIMIT, HIGH_RISK_MIN, MEDIUM_RISK_LIMIT, RISK_MAX),
            lines = listOf(
                FuzzyGraphLine(
                    label = RiskLevel.LOW.label,
                    points = listOf(
                        FuzzyGraphPoint(0.0, 1.0),
                        FuzzyGraphPoint(LOW_RISK_LIMIT, 0.0),
                        FuzzyGraphPoint(RISK_MAX, 0.0)
                    )
                ),
                FuzzyGraphLine(
                    label = RiskLevel.MEDIUM.label,
                    points = listOf(
                        FuzzyGraphPoint(0.0, 0.0),
                        FuzzyGraphPoint(MEDIUM_RISK_MIN, 0.0),
                        FuzzyGraphPoint(MEDIUM_RISK_LIMIT, 1.0),
                        FuzzyGraphPoint(RISK_MAX, 1.0)
                    )
                ),
                FuzzyGraphLine(
                    label = RiskLevel.HIGH.label,
                    points = listOf(
                        FuzzyGraphPoint(0.0, 0.0),
                        FuzzyGraphPoint(HIGH_RISK_MIN, 0.0),
                        FuzzyGraphPoint(RISK_MAX, 1.0)
                    )
                )
            )
        )
    }

    private fun memberships(value: Double, domain: InputDomain): Map<FuzzySet, Double> {
        val low = when {
            value <= domain.lowMax -> 1.0
            value >= domain.midPeak -> 0.0
            else -> (domain.midPeak - value) / (domain.midPeak - domain.lowMax)
        }
        val medium = when {
            value <= domain.lowMax || value >= domain.highMin -> 0.0
            value == domain.midPeak -> 1.0
            value < domain.midPeak -> (value - domain.lowMax) / (domain.midPeak - domain.lowMax)
            else -> (domain.highMin - value) / (domain.highMin - domain.midPeak)
        }
        val high = when {
            value <= domain.midPeak -> 0.0
            value >= domain.highMin -> 1.0
            else -> (value - domain.midPeak) / (domain.highMin - domain.midPeak)
        }
        return mapOf(
            FuzzySet.LOW to low.coerce(),
            FuzzySet.MEDIUM to medium.coerce(),
            FuzzySet.HIGH to high.coerce()
        )
    }

    private fun inferRisk(sales: FuzzySet, stock: FuzzySet, demand: FuzzySet): RiskLevel {
        val score = sales.salesRiskScore + stock.stockRiskScore + demand.demandRiskScore
        return when {
            score <= 2 -> RiskLevel.LOW
            score <= 4 -> RiskLevel.MEDIUM
            else -> RiskLevel.HIGH
        }
    }

    private fun crispZ(risk: RiskLevel, alpha: Double): Double {
        return when (risk) {
            RiskLevel.LOW -> LOW_RISK_LIMIT - (alpha * LOW_RISK_LIMIT)
            RiskLevel.MEDIUM -> MEDIUM_RISK_MIN + (alpha * (MEDIUM_RISK_LIMIT - MEDIUM_RISK_MIN))
            RiskLevel.HIGH -> HIGH_RISK_MIN + (alpha * (RISK_MAX - HIGH_RISK_MIN))
        }
    }

    private fun buildFuzzification(
        salesSets: Map<FuzzySet, Double>,
        stockSets: Map<FuzzySet, Double>,
        demandSets: Map<FuzzySet, Double>,
        sales: Int,
        stock: Int,
        demand: Int
    ): String {
        return listOf(
            "Derajat keanggotaan penjualan:",
            buildMembershipEquation("penjualan", sales.toDouble(), SALES_DOMAIN, salesSets),
            "",
            "Derajat keanggotaan stok:",
            buildMembershipEquation("stok", stock.toDouble(), STOCK_DOMAIN, stockSets),
            "",
            "Derajat keanggotaan permintaan:",
            buildMembershipEquation("permintaan", demand.toDouble(), DEMAND_DOMAIN, demandSets)
        ).joinToString("\n")
    }

    private fun buildInference(rules: List<RuleResult>): String {
        if (rules.isEmpty()) return "Tidak ada aturan aktif. Sistem menggunakan nilai risiko tengah."
        return rules.sortedByDescending { it.alpha }.mapIndexed { index, rule ->
            "R${index + 1}: IF Penjualan ${rule.sales.label} AND Stok ${rule.stock.label} AND Permintaan ${rule.demand.label} THEN Risiko ${rule.risk.label} | alpha=${format(rule.alpha)}, z=${format(rule.z)}"
        }.joinToString("\n")
    }

    private fun buildDefuzzification(
        rules: List<RuleResult>,
        sumWeightedZ: Double,
        sumAlpha: Double,
        z: Double,
        level: RiskLevel
    ): String {
        if (sumAlpha == 0.0) {
            return "Tidak ada alpha aktif.\nZ default = ${format(z)}\nKategori = ${level.label}"
        }
        val numerator = rules.joinToString(" + ") { "(${format(it.alpha)} x ${format(it.z)})" }
        val denominator = rules.joinToString(" + ") { format(it.alpha) }
        return listOf(
            "Z = sum(alpha * z) / sum(alpha)",
            "Z = ($numerator) / ($denominator)",
            "sum(alpha * z) = ${format(sumWeightedZ)}",
            "sum(alpha) = ${format(sumAlpha)}",
            "Z = ${format(sumWeightedZ)} / ${format(sumAlpha)}",
            "Z = ${format(z)}",
            "Kategori = ${level.label}"
        ).joinToString("\n")
    }

    private fun formatSet(values: Map<FuzzySet, Double>): String {
        return values.entries.joinToString(", ") { "${it.key.label}=${format(it.value)}" }
    }

    private fun buildMembershipEquation(
        variableName: String,
        value: Double,
        domain: InputDomain,
        memberships: Map<FuzzySet, Double>
    ): String {
        return listOf(
            membershipLine(variableName, FuzzySet.LOW, value, memberships.getValue(FuzzySet.LOW), domain),
            membershipLine(variableName, FuzzySet.MEDIUM, value, memberships.getValue(FuzzySet.MEDIUM), domain),
            membershipLine(variableName, FuzzySet.HIGH, value, memberships.getValue(FuzzySet.HIGH), domain),
            "Hasil: ${formatSet(memberships)}"
        ).joinToString("\n")
    }

    private fun membershipLine(
        variableName: String,
        set: FuzzySet,
        value: Double,
        mu: Double,
        domain: InputDomain
    ): String {
        val variable = "$variableName ${set.label.lowercase(Locale.US)}"
        return when (set) {
            FuzzySet.LOW -> when {
                value <= domain.lowMax -> "mu $variable(${formatValue(value)}) = 1, karena x <= ${formatValue(domain.lowMax)}"
                value >= domain.midPeak -> "mu $variable(${formatValue(value)}) = 0, karena x >= ${formatValue(domain.midPeak)}"
                else -> "mu $variable(${formatValue(value)}) = (${formatValue(domain.midPeak)} - ${formatValue(value)}) / (${formatValue(domain.midPeak)} - ${formatValue(domain.lowMax)}) = ${format(mu)}"
            }
            FuzzySet.MEDIUM -> when {
                value <= domain.lowMax -> "mu $variable(${formatValue(value)}) = 0, karena x <= ${formatValue(domain.lowMax)}"
                value >= domain.highMin -> "mu $variable(${formatValue(value)}) = 0, karena x >= ${formatValue(domain.highMin)}"
                value == domain.midPeak -> "mu $variable(${formatValue(value)}) = 1, karena x = ${formatValue(domain.midPeak)}"
                value < domain.midPeak -> "mu $variable(${formatValue(value)}) = (${formatValue(value)} - ${formatValue(domain.lowMax)}) / (${formatValue(domain.midPeak)} - ${formatValue(domain.lowMax)}) = ${format(mu)}"
                else -> "mu $variable(${formatValue(value)}) = (${formatValue(domain.highMin)} - ${formatValue(value)}) / (${formatValue(domain.highMin)} - ${formatValue(domain.midPeak)}) = ${format(mu)}"
            }
            FuzzySet.HIGH -> when {
                value <= domain.midPeak -> "mu $variable(${formatValue(value)}) = 0, karena x <= ${formatValue(domain.midPeak)}"
                value >= domain.highMin -> "mu $variable(${formatValue(value)}) = 1, karena x >= ${formatValue(domain.highMin)}"
                else -> "mu $variable(${formatValue(value)}) = (${formatValue(value)} - ${formatValue(domain.midPeak)}) / (${formatValue(domain.highMin)} - ${formatValue(domain.midPeak)}) = ${format(mu)}"
            }
        }
    }

    private fun inputGraphSpec(
        title: String,
        value: Double,
        currentLabel: String,
        domain: InputDomain
    ): FuzzyGraphSpec {
        val xMax = max(domain.highMin, value).let { maxValue ->
            if (maxValue <= 0.0) domain.highMin else maxValue * 1.08
        }
        return FuzzyGraphSpec(
            title = title,
            currentValue = value,
            currentLabel = currentLabel,
            xMin = 0.0,
            xMax = xMax,
            xTicks = listOf(0.0, domain.lowMax, domain.midPeak, domain.highMin),
            lines = listOf(
                FuzzyGraphLine(
                    label = FuzzySet.LOW.label,
                    points = listOf(
                        FuzzyGraphPoint(0.0, 1.0),
                        FuzzyGraphPoint(domain.lowMax, 1.0),
                        FuzzyGraphPoint(domain.midPeak, 0.0),
                        FuzzyGraphPoint(xMax, 0.0)
                    )
                ),
                FuzzyGraphLine(
                    label = FuzzySet.MEDIUM.label,
                    points = listOf(
                        FuzzyGraphPoint(0.0, 0.0),
                        FuzzyGraphPoint(domain.lowMax, 0.0),
                        FuzzyGraphPoint(domain.midPeak, 1.0),
                        FuzzyGraphPoint(domain.highMin, 0.0),
                        FuzzyGraphPoint(xMax, 0.0)
                    )
                ),
                FuzzyGraphLine(
                    label = FuzzySet.HIGH.label,
                    points = listOf(
                        FuzzyGraphPoint(0.0, 0.0),
                        FuzzyGraphPoint(domain.midPeak, 0.0),
                        FuzzyGraphPoint(domain.highMin, 1.0),
                        FuzzyGraphPoint(xMax, 1.0)
                    )
                )
            )
        )
    }

    private fun format(value: Double): String = String.format(Locale.US, "%.2f", value)

    private fun formatValue(value: Double): String {
        return if (value % 1.0 == 0.0) value.toInt().toString() else format(value)
    }

    private fun Double.coerce(): Double = max(0.0, min(1.0, this))

    private val SALES_DOMAIN = InputDomain(lowMax = 50.0, midPeak = 110.0, highMin = 180.0)
    private val STOCK_DOMAIN = InputDomain(lowMax = 30.0, midPeak = 90.0, highMin = 160.0)
    private val DEMAND_DOMAIN = InputDomain(lowMax = 60.0, midPeak = 130.0, highMin = 220.0)

    private const val LOW_RISK_LIMIT = 40.0
    private const val MEDIUM_RISK_MIN = 30.0
    private const val MEDIUM_RISK_LIMIT = 70.0
    private const val HIGH_RISK_MIN = 60.0
    private const val RISK_MAX = 100.0
    private const val DEFAULT_RISK_Z = 50.0
}

private data class RuleResult(
    val sales: FuzzySet,
    val stock: FuzzySet,
    val demand: FuzzySet,
    val risk: RiskLevel,
    val alpha: Double,
    val z: Double
)

private data class InputDomain(
    val lowMax: Double,
    val midPeak: Double,
    val highMin: Double
)

private enum class FuzzySet(
    val label: String,
    val salesRiskScore: Int,
    val stockRiskScore: Int,
    val demandRiskScore: Int
) {
    LOW("Rendah", salesRiskScore = 2, stockRiskScore = 0, demandRiskScore = 2),
    MEDIUM("Sedang", salesRiskScore = 1, stockRiskScore = 1, demandRiskScore = 1),
    HIGH("Tinggi", salesRiskScore = 0, stockRiskScore = 2, demandRiskScore = 0)
}
