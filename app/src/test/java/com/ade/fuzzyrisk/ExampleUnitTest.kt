package com.ade.fuzzyrisk

import com.ade.fuzzyrisk.domain.FuzzyTsukamotoCalculator
import com.ade.fuzzyrisk.domain.RiskLevel
import org.junit.Test

import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun calculate_highSalesLowStockHighDemand_returnsLowestRisk() {
        val result = FuzzyTsukamotoCalculator.calculate(sales = 190, stock = 20, demand = 230)

        assertEquals(RiskLevel.LOW, result.riskLevel)
        assertEquals(0.0, result.zValue, 0.0001)
        assertTrue(result.fuzzification.contains("Derajat keanggotaan penjualan"))
        assertTrue(result.inference.contains("THEN Risiko Rendah"))
        assertTrue(result.defuzzification.contains("sum(alpha)"))
    }

    @Test
    fun calculate_lowSalesHighStockLowDemand_returnsHighestRisk() {
        val result = FuzzyTsukamotoCalculator.calculate(sales = 40, stock = 170, demand = 50)

        assertEquals(RiskLevel.HIGH, result.riskLevel)
        assertEquals(100.0, result.zValue, 0.0001)
        assertTrue(result.inference.contains("THEN Risiko Tinggi"))
    }

    @Test
    fun calculate_middlePeaks_returnsMediumRisk() {
        val result = FuzzyTsukamotoCalculator.calculate(sales = 110, stock = 90, demand = 130)

        assertEquals(RiskLevel.MEDIUM, result.riskLevel)
        assertEquals(70.0, result.zValue, 0.0001)
        assertEquals(RiskLevel.MEDIUM, FuzzyTsukamotoCalculator.riskLevelFromZ(result.zValue))
        assertTrue(result.inference.contains("THEN Risiko Sedang"))
    }

    @Test
    fun calculate_repeatedSameInput_returnsSameResult() {
        val first = FuzzyTsukamotoCalculator.calculate(sales = 85, stock = 120, demand = 95)
        val second = FuzzyTsukamotoCalculator.calculate(sales = 85, stock = 120, demand = 95)

        assertEquals(first.riskLevel, second.riskLevel)
        assertEquals(first.zValue, second.zValue, 0.0001)
        assertEquals(first.fuzzification, second.fuzzification)
        assertEquals(first.inference, second.inference)
        assertEquals(first.defuzzification, second.defuzzification)
    }

    @Test
    fun calculate_allValidInputs_keepZInsideRiskUniverse() {
        val samples = listOf(
            Triple(0, 0, 0),
            Triple(50, 30, 60),
            Triple(90, 90, 120),
            Triple(110, 90, 130),
            Triple(150, 120, 180),
            Triple(180, 160, 220),
            Triple(250, 250, 250)
        )

        samples.forEach { (sales, stock, demand) ->
            val result = FuzzyTsukamotoCalculator.calculate(sales, stock, demand)
            assertTrue("Z harus di rentang 0..100 untuk $sales/$stock/$demand", result.zValue in 0.0..100.0)
            assertEquals(FuzzyTsukamotoCalculator.riskLevelFromZ(result.zValue), result.riskLevel)
        }
    }

    @Test
    fun calculate_negativeInput_throwsError() {
        assertThrows(IllegalArgumentException::class.java) {
            FuzzyTsukamotoCalculator.calculate(sales = -1, stock = 10, demand = 10)
        }
    }
}
