package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GstHistoryRepository
import com.example.data.PlayStoreDocsContent
import com.example.domain.BusinessProfile
import com.example.domain.GstCalculationInput
import com.example.domain.GstCalculationResult
import com.example.domain.GstCalculatorEngine
import com.example.domain.HsnProductItem
import com.example.domain.SavedGstCalculation
import com.example.domain.SupplyType
import com.example.domain.TaxMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

data class CalculatorUiState(
    val amountText: String = "10000",
    val selectedPresetRate: BigDecimal? = BigDecimal("18"),
    val isCustomRateSelected: Boolean = false,
    val customRateText: String = "18",
    val cessRateText: String = "0",
    val quantityText: String = "1",
    val discountText: String = "0",
    val showAdvancedOptions: Boolean = false,
    val showAllSlabsComparison: Boolean = false,
    val taxMode: TaxMode = TaxMode.EXCLUSIVE,
    val supplyType: SupplyType = SupplyType.INTRA_STATE,
    val currencySymbol: String = "₹",
    val activeHsnCode: String = "",
    val activeProductTitle: String = "",
    val result: GstCalculationResult = GstCalculatorEngine.calculate(
        GstCalculationInput(
            amount = BigDecimal("10000"),
            gstRatePercent = BigDecimal("18")
        )
    ),
    val savedHistory: List<SavedGstCalculation> = emptyList(),
    val businessProfile: BusinessProfile = BusinessProfile(),
    val checklistStates: Map<String, Boolean> = PlayStoreDocsContent.checklistItems.associate { it.id to it.defaultDone }
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GstHistoryRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.savedCalculationsFlow,
                repository.businessProfileFlow,
                repository.currencySymbolFlow,
                repository.checklistStatesFlow
            ) { history, profile, currency, checklist ->
                Quad(history, profile, currency, checklist)
            }.collect { (history, profile, currency, savedChecklist) ->
                val mergedChecklist = PlayStoreDocsContent.checklistItems.associate { item ->
                    item.id to (savedChecklist[item.id] ?: item.defaultDone)
                }
                _uiState.update { state ->
                    val updated = state.copy(
                        savedHistory = history,
                        businessProfile = profile,
                        currencySymbol = currency,
                        checklistStates = mergedChecklist
                    )
                    recalculate(updated)
                }
            }
        }
    }

    fun onAmountChange(newValue: String) {
        val sanitized = sanitizeDecimalInput(newValue)
        _uiState.update { recalculate(it.copy(amountText = sanitized)) }
    }

    fun addQuickAmount(delta: Long) {
        val current = _uiState.value.amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val updated = current.add(BigDecimal(delta)).stripTrailingZeros().toPlainString()
        _uiState.update { recalculate(it.copy(amountText = updated)) }
    }

    fun clearAmount() {
        _uiState.update { recalculate(it.copy(amountText = "")) }
    }

    fun setAmountFromExternal(value: BigDecimal) {
        val positive = value.abs().stripTrailingZeros().toPlainString()
        _uiState.update { recalculate(it.copy(amountText = positive)) }
    }

    fun applyHsnProduct(item: HsnProductItem) {
        val matchingPreset = GstCalculatorEngine.STANDARD_SLABS.firstOrNull {
            it.compareTo(item.gstRate) == 0
        }
        _uiState.update { state ->
            recalculate(
                state.copy(
                    selectedPresetRate = matchingPreset,
                    isCustomRateSelected = matchingPreset == null,
                    customRateText = GstCalculatorEngine.formatRate(item.gstRate),
                    activeHsnCode = item.hsnCode,
                    activeProductTitle = item.productName
                )
            )
        }
    }

    fun clearActiveHsn() {
        _uiState.update { it.copy(activeHsnCode = "", activeProductTitle = "") }
    }

    fun onSelectStandardRate(rate: BigDecimal) {
        _uiState.update {
            recalculate(
                it.copy(
                    selectedPresetRate = rate,
                    isCustomRateSelected = false,
                    customRateText = GstCalculatorEngine.formatRate(rate)
                )
            )
        }
    }

    fun onSelectCustomRateMode() {
        _uiState.update {
            recalculate(
                it.copy(
                    selectedPresetRate = null,
                    isCustomRateSelected = true
                )
            )
        }
    }

    fun onCustomRateChange(newValue: String) {
        val sanitized = sanitizeDecimalInput(newValue)
        _uiState.update {
            recalculate(
                it.copy(
                    isCustomRateSelected = true,
                    selectedPresetRate = null,
                    customRateText = sanitized
                )
            )
        }
    }

    fun onCessRateChange(newValue: String) {
        val sanitized = sanitizeDecimalInput(newValue)
        _uiState.update { recalculate(it.copy(cessRateText = sanitized)) }
    }

    fun onQuantityChange(newValue: String) {
        val digitsOnly = newValue.filter { it.isDigit() }.take(6)
        _uiState.update { recalculate(it.copy(quantityText = digitsOnly)) }
    }

    fun onDiscountChange(newValue: String) {
        val sanitized = sanitizeDecimalInput(newValue)
        _uiState.update { recalculate(it.copy(discountText = sanitized)) }
    }

    fun onTaxModeChange(mode: TaxMode) {
        _uiState.update { recalculate(it.copy(taxMode = mode)) }
    }

    fun onSupplyTypeChange(supplyType: SupplyType) {
        _uiState.update { recalculate(it.copy(supplyType = supplyType)) }
    }

    fun toggleAdvancedOptions() {
        _uiState.update { it.copy(showAdvancedOptions = !it.showAdvancedOptions) }
    }

    fun toggleAllSlabsComparison() {
        _uiState.update { it.copy(showAllSlabsComparison = !it.showAllSlabsComparison) }
    }

    fun onCurrencyChange(symbol: String) {
        viewModelScope.launch {
            repository.saveCurrencySymbol(symbol)
        }
    }

    fun saveCurrentCalculation(
        title: String = "",
        partyName: String = "",
        hsnCode: String = ""
    ) {
        val state = _uiState.value
        val res = state.result
        val cleanTitle = title.trim().ifBlank {
            state.activeProductTitle.ifBlank {
                "${GstCalculatorEngine.formatRate(res.input.gstRatePercent)}% GST • ${res.input.taxMode.shortLabel}"
            }
        }
        val item = SavedGstCalculation(
            id = UUID.randomUUID().toString(),
            title = cleanTitle,
            partyName = partyName.trim(),
            hsnCode = hsnCode.trim().ifBlank { state.activeHsnCode },
            timestampMillis = System.currentTimeMillis(),
            amount = res.input.amount.toPlainString(),
            gstRatePercent = res.input.gstRatePercent.toPlainString(),
            cessRatePercent = res.input.cessRatePercent.toPlainString(),
            quantity = res.input.quantity,
            discountPercent = res.input.discountPercent.toPlainString(),
            taxMode = res.input.taxMode,
            supplyType = res.input.supplyType,
            currencySymbol = res.input.currencySymbol,
            netTaxableFormatted = GstCalculatorEngine.formatMoney(res.netTaxableAmount, res.input.currencySymbol),
            totalTaxFormatted = GstCalculatorEngine.formatMoney(res.totalTaxWithCess, res.input.currencySymbol),
            grandTotalFormatted = GstCalculatorEngine.formatMoney(res.grandTotalAmount, res.input.currencySymbol)
        )
        viewModelScope.launch {
            repository.saveCalculation(item)
        }
    }

    fun loadSavedCalculation(saved: SavedGstCalculation): GstCalculationResult {
        val rateDecimal = saved.gstRatePercent.toBigDecimalOrNull() ?: BigDecimal("18")
        val matchingPreset = GstCalculatorEngine.STANDARD_SLABS.firstOrNull {
            it.compareTo(rateDecimal) == 0
        }
        val showAdv = saved.quantity > 1 ||
            (saved.discountPercent.toBigDecimalOrNull() ?: BigDecimal.ZERO) > BigDecimal.ZERO ||
            (saved.cessRatePercent.toBigDecimalOrNull() ?: BigDecimal.ZERO) > BigDecimal.ZERO

        var computed = _uiState.value.result
        _uiState.update { state ->
            val next = state.copy(
                amountText = saved.amount,
                selectedPresetRate = matchingPreset,
                isCustomRateSelected = matchingPreset == null,
                customRateText = GstCalculatorEngine.formatRate(rateDecimal),
                cessRateText = saved.cessRatePercent,
                quantityText = saved.quantity.toString(),
                discountText = saved.discountPercent,
                showAdvancedOptions = showAdv,
                taxMode = saved.taxMode,
                supplyType = saved.supplyType,
                currencySymbol = saved.currencySymbol
            )
            val recalculated = recalculate(next)
            computed = recalculated.result
            recalculated
        }
        return computed
    }

    fun buildResultFromSaved(saved: SavedGstCalculation): GstCalculationResult {
        return GstCalculatorEngine.calculate(
            GstCalculationInput(
                amount = saved.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                gstRatePercent = saved.gstRatePercent.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                cessRatePercent = saved.cessRatePercent.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                quantity = saved.quantity.coerceAtLeast(1),
                discountPercent = saved.discountPercent.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                taxMode = saved.taxMode,
                supplyType = saved.supplyType,
                currencySymbol = saved.currencySymbol
            )
        )
    }

    fun deleteSavedCalculation(id: String) {
        viewModelScope.launch {
            repository.deleteCalculation(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllCalculations()
        }
    }

    fun saveBusinessProfile(profile: BusinessProfile) {
        viewModelScope.launch {
            repository.saveBusinessProfile(profile)
        }
    }

    fun toggleChecklistItem(itemId: String, checked: Boolean) {
        viewModelScope.launch {
            repository.toggleChecklistItem(itemId, checked)
        }
    }

    private fun recalculate(state: CalculatorUiState): CalculatorUiState {
        val amount = state.amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val gstRate = if (state.isCustomRateSelected) {
            state.customRateText.toBigDecimalOrNull() ?: BigDecimal.ZERO
        } else {
            state.selectedPresetRate ?: BigDecimal.ZERO
        }
        val cessRate = state.cessRateText.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val qty = state.quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val discount = state.discountText.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val result = GstCalculatorEngine.calculate(
            GstCalculationInput(
                amount = amount,
                gstRatePercent = gstRate,
                cessRatePercent = cessRate,
                quantity = qty,
                discountPercent = discount,
                taxMode = state.taxMode,
                supplyType = state.supplyType,
                currencySymbol = state.currencySymbol
            )
        )
        return state.copy(result = result)
    }

    private fun sanitizeDecimalInput(raw: String): String {
        val filtered = raw.filter { it.isDigit() || it == '.' }
        val firstDot = filtered.indexOf('.')
        val singleDot = if (firstDot >= 0) {
            filtered.substring(0, firstDot + 1) + filtered.substring(firstDot + 1).replace(".", "")
        } else {
            filtered
        }
        return singleDot.take(14)
    }

    private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
}
