package com.ade.fuzzyrisk.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ade.fuzzyrisk.data.SalesRecord
import com.ade.fuzzyrisk.data.SalesRepository
import com.ade.fuzzyrisk.domain.FuzzyResult
import com.ade.fuzzyrisk.domain.FuzzyTsukamotoCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FuzzyRiskUiState(
    val records: List<SalesRecord> = emptyList(),
    val latestResult: FuzzyResult? = null
)

class FuzzyRiskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SalesRepository(application)

    val uiState: StateFlow<FuzzyRiskUiState> = repository.records
        .map { records -> FuzzyRiskUiState(records = records) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FuzzyRiskUiState())

    fun calculate(sales: Int, stock: Int, demand: Int): FuzzyResult {
        return FuzzyTsukamotoCalculator.calculate(sales, stock, demand)
    }

    fun save(phoneType: String, sales: Int, stock: Int, demand: Int, result: FuzzyResult) {
        viewModelScope.launch {
            repository.insert(
                SalesRecord(
                    phoneType = phoneType,
                    sales = sales,
                    stock = stock,
                    demand = demand,
                    riskLevel = result.riskLevel.label,
                    zValue = result.zValue,
                    fuzzification = result.fuzzification,
                    inference = result.inference,
                    defuzzification = result.defuzzification
                )
            )
        }
    }
}
