package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.domain.BasicCalcHistoryItem
import com.example.domain.BasicCalculatorEngine
import com.example.domain.ConverterCategory
import com.example.domain.ConverterUnit
import com.example.domain.UnitConversionRow
import com.example.domain.UnitConverterEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.util.UUID

data class BasicCalcUiState(
    val expression: String = "0",
    val liveResult: String = "0",
    val latestNumericValue: BigDecimal? = BigDecimal.ZERO,
    val errorText: String? = null,
    val justEvaluated: Boolean = false,
    val history: List<BasicCalcHistoryItem> = emptyList()
)

data class UnitConverterUiState(
    val selectedCategory: ConverterCategory = ConverterCategory.LENGTH,
    val inputValue: String = "1",
    val fromUnit: ConverterUnit = UnitConverterEngine.getUnits(ConverterCategory.LENGTH)[2], // Meter
    val toUnit: ConverterUnit = UnitConverterEngine.getUnits(ConverterCategory.LENGTH)[5],   // Foot
    val convertedResult: String = UnitConverterEngine.convertSingle(
        ConverterCategory.LENGTH,
        "1",
        UnitConverterEngine.getUnits(ConverterCategory.LENGTH)[2],
        UnitConverterEngine.getUnits(ConverterCategory.LENGTH)[5]
    ),
    val allRows: List<UnitConversionRow> = UnitConverterEngine.convertAll(
        ConverterCategory.LENGTH,
        "1",
        UnitConverterEngine.getUnits(ConverterCategory.LENGTH)[2]
    )
)

class ToolsViewModel : ViewModel() {

    private val _basicState = MutableStateFlow(BasicCalcUiState())
    val basicState: StateFlow<BasicCalcUiState> = _basicState.asStateFlow()

    private val _converterState = MutableStateFlow(UnitConverterUiState())
    val converterState: StateFlow<UnitConverterUiState> = _converterState.asStateFlow()

    // --- Basic Calculator Operations ---

    fun onDigitOrDot(symbol: String) {
        _basicState.update { state ->
            val base = if (state.justEvaluated) "0" else state.expression
            val next = when {
                symbol == "." -> {
                    val lastSegment = base.split('+', '−', '×', '÷', '(', ')').lastOrNull().orEmpty()
                    if (lastSegment.contains('.')) base else if (lastSegment.isEmpty()) "${base}0." else "$base."
                }
                base == "0" -> symbol
                else -> "$base$symbol"
            }
            updateBasicPreview(state.copy(expression = next, justEvaluated = false, errorText = null))
        }
    }

    fun onOperator(op: String) {
        _basicState.update { state ->
            val current = state.expression
            if (current.isEmpty()) return@update state
            val operators = setOf('+', '−', '×', '÷')
            val lastChar = current.last()
            val next = when {
                lastChar in operators -> current.dropLast(1) + op
                lastChar == '(' && op != "−" -> current
                else -> "$current$op"
            }
            updateBasicPreview(state.copy(expression = next, justEvaluated = false, errorText = null))
        }
    }

    fun onPercent() {
        _basicState.update { state ->
            val current = state.expression
            if (current.isEmpty()) return@update state
            val lastChar = current.last()
            if (lastChar.isDigit() || lastChar == ')') {
                updateBasicPreview(state.copy(expression = "$current%", justEvaluated = false, errorText = null))
            } else {
                state
            }
        }
    }

    fun onToggleParenthesis() {
        _basicState.update { state ->
            val current = if (state.justEvaluated) "" else state.expression
            val openCount = current.count { it == '(' }
            val closeCount = current.count { it == ')' }
            val last = current.lastOrNull()
            val next = when {
                current == "0" || current.isEmpty() -> "("
                openCount > closeCount && last != null && (last.isDigit() || last == ')' || last == '%') -> "$current)"
                else -> "$current("
            }
            updateBasicPreview(state.copy(expression = next, justEvaluated = false, errorText = null))
        }
    }

    fun onToggleSign() {
        _basicState.update { state ->
            val current = state.expression
            if (current == "0" || current.isEmpty()) return@update state
            val next = if (current.startsWith("−") && current.drop(1).none { it in setOf('+', '−', '×', '÷') }) {
                current.drop(1)
            } else if (current.none { it in setOf('+', '−', '×', '÷', '(', ')') }) {
                "−$current"
            } else {
                "−($current)"
            }
            updateBasicPreview(state.copy(expression = next, justEvaluated = false, errorText = null))
        }
    }

    fun onBackspace() {
        _basicState.update { state ->
            if (state.justEvaluated) {
                return@update updateBasicPreview(state.copy(expression = "0", justEvaluated = false, errorText = null))
            }
            val next = if (state.expression.length <= 1) "0" else state.expression.dropLast(1)
            updateBasicPreview(state.copy(expression = next, errorText = null))
        }
    }

    fun onAllClear() {
        _basicState.update {
            it.copy(
                expression = "0",
                liveResult = "0",
                latestNumericValue = BigDecimal.ZERO,
                errorText = null,
                justEvaluated = false
            )
        }
    }

    fun onEquals() {
        _basicState.update { state ->
            val evaluated = BasicCalculatorEngine.evaluate(state.expression)
            if (evaluated == null) {
                state.copy(errorText = "Invalid expression")
            } else {
                val formatted = BasicCalculatorEngine.formatResult(evaluated)
                val plain = BasicCalculatorEngine.toPlainInputString(evaluated)
                val historyItem = BasicCalcHistoryItem(
                    id = UUID.randomUUID().toString(),
                    expression = state.expression,
                    resultFormatted = formatted,
                    rawResult = evaluated
                )
                val updatedHistory = if (state.expression != plain) {
                    (listOf(historyItem) + state.history).take(20)
                } else {
                    state.history
                }
                state.copy(
                    expression = plain,
                    liveResult = formatted,
                    latestNumericValue = evaluated,
                    errorText = null,
                    justEvaluated = true,
                    history = updatedHistory
                )
            }
        }
    }

    fun onUseHistoryItem(item: BasicCalcHistoryItem) {
        _basicState.update {
            val plain = BasicCalculatorEngine.toPlainInputString(item.rawResult)
            updateBasicPreview(
                it.copy(
                    expression = plain,
                    justEvaluated = false,
                    errorText = null
                )
            )
        }
    }

    fun clearBasicHistory() {
        _basicState.update { it.copy(history = emptyList()) }
    }

    private fun updateBasicPreview(state: BasicCalcUiState): BasicCalcUiState {
        val evaluated = BasicCalculatorEngine.evaluate(state.expression)
        return if (evaluated != null) {
            state.copy(
                liveResult = BasicCalculatorEngine.formatResult(evaluated),
                latestNumericValue = evaluated
            )
        } else {
            state
        }
    }

    // --- Unit Converter Operations ---

    fun selectCategory(category: ConverterCategory) {
        val units = UnitConverterEngine.getUnits(category)
        val defaultFrom = when (category) {
            ConverterCategory.LENGTH -> units.getOrElse(2) { units.first() } // m
            ConverterCategory.WEIGHT -> units.getOrElse(2) { units.first() } // kg
            ConverterCategory.TEMPERATURE -> units.first() // C
            ConverterCategory.DATA -> units.getOrElse(2) { units.first() } // MB
            else -> units.first()
        }
        val defaultTo = when (category) {
            ConverterCategory.LENGTH -> units.getOrElse(5) { units.last() } // ft
            ConverterCategory.WEIGHT -> units.getOrElse(6) { units.last() } // lb
            ConverterCategory.TEMPERATURE -> units.getOrElse(1) { units.last() } // F
            ConverterCategory.DATA -> units.getOrElse(3) { units.last() } // GB
            else -> units.getOrElse(1) { units.first() }
        }
        val sanitizedInput = if (category == ConverterCategory.NUMBER_BASE) "255" else "1"
        _converterState.update {
            recomputeConverter(
                it.copy(
                    selectedCategory = category,
                    inputValue = sanitizedInput,
                    fromUnit = defaultFrom,
                    toUnit = defaultTo
                )
            )
        }
    }

    fun onConverterInputChange(raw: String) {
        _converterState.update { state ->
            val sanitized = if (state.selectedCategory == ConverterCategory.NUMBER_BASE) {
                val allowed = when (state.fromUnit.radix) {
                    2 -> setOf('0', '1', '-')
                    8 -> ('0'..'7').toSet() + '-'
                    16 -> (('0'..'9') + ('a'..'f') + ('A'..'F')).toSet() + '-'
                    else -> ('0'..'9').toSet() + '-'
                }
                raw.filter { it in allowed }.uppercase().take(18)
            } else {
                val allowNeg = state.selectedCategory == ConverterCategory.TEMPERATURE
                val filtered = raw.filterIndexed { index, c ->
                    c.isDigit() || c == '.' || (allowNeg && c == '-' && index == 0)
                }
                val dotIdx = filtered.indexOf('.')
                if (dotIdx >= 0) {
                    filtered.substring(0, dotIdx + 1) + filtered.substring(dotIdx + 1).replace(".", "")
                } else {
                    filtered
                }.take(16)
            }
            recomputeConverter(state.copy(inputValue = sanitized))
        }
    }

    fun selectFromUnit(unit: ConverterUnit) {
        _converterState.update { state ->
            val cleanedInput = if (state.selectedCategory == ConverterCategory.NUMBER_BASE) {
                if (state.inputValue.toBigIntegerOrNull(unit.radix) == null) "0" else state.inputValue
            } else {
                state.inputValue
            }
            recomputeConverter(state.copy(fromUnit = unit, inputValue = cleanedInput))
        }
    }

    fun selectToUnit(unit: ConverterUnit) {
        _converterState.update { state ->
            recomputeConverter(state.copy(toUnit = unit))
        }
    }

    fun swapConverterUnits() {
        _converterState.update { state ->
            recomputeConverter(
                state.copy(
                    fromUnit = state.toUnit,
                    toUnit = state.fromUnit
                )
            )
        }
    }

    private fun recomputeConverter(state: UnitConverterUiState): UnitConverterUiState {
        val single = UnitConverterEngine.convertSingle(
            category = state.selectedCategory,
            rawInput = state.inputValue,
            fromUnit = state.fromUnit,
            toUnit = state.toUnit
        )
        val rows = UnitConverterEngine.convertAll(
            category = state.selectedCategory,
            rawInput = state.inputValue,
            fromUnit = state.fromUnit
        )
        return state.copy(convertedResult = single, allRows = rows)
    }
}
