package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.BasicCalcHistoryItem
import com.example.ui.BasicCalcUiState
import com.example.ui.components.ElemensysBrandHeader
import com.example.ui.theme.FormulaTextStyle
import java.math.BigDecimal

@Composable
fun BasicCalculatorScreen(
    uiState: BasicCalcUiState,
    onDigitOrDot: (String) -> Unit,
    onOperator: (String) -> Unit,
    onPercent: () -> Unit,
    onToggleParenthesis: () -> Unit,
    onToggleSign: () -> Unit,
    onBackspace: () -> Unit,
    onAllClear: () -> Unit,
    onEquals: () -> Unit,
    onUseHistoryItem: (BasicCalcHistoryItem) -> Unit,
    onClearHistory: () -> Unit,
    onCopyResult: (String) -> Unit,
    onSendToGst: (BigDecimal) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ElemensysBrandHeader(
            subtitle = "Basic Arithmetic Calculator & Quick GST Transfer"
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth >= 720.dp
            if (isWide) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CalculatorDisplayCard(
                            uiState = uiState,
                            onCopyResult = { onCopyResult(uiState.liveResult) },
                            onSendToGst = {
                                uiState.latestNumericValue?.let { onSendToGst(it) }
                            }
                        )
                        BasicCalcHistoryStrip(
                            history = uiState.history,
                            onUseItem = onUseHistoryItem,
                            onClearHistory = onClearHistory
                        )
                    }
                    CalculatorKeypad(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        onDigitOrDot = onDigitOrDot,
                        onOperator = onOperator,
                        onPercent = onPercent,
                        onToggleParenthesis = onToggleParenthesis,
                        onToggleSign = onToggleSign,
                        onBackspace = onBackspace,
                        onAllClear = onAllClear,
                        onEquals = onEquals
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CalculatorDisplayCard(
                        uiState = uiState,
                        onCopyResult = { onCopyResult(uiState.liveResult) },
                        onSendToGst = {
                            uiState.latestNumericValue?.let { onSendToGst(it) }
                        }
                    )
                    if (uiState.history.isNotEmpty()) {
                        BasicCalcHistoryStrip(
                            history = uiState.history,
                            onUseItem = onUseHistoryItem,
                            onClearHistory = onClearHistory
                        )
                    }
                    CalculatorKeypad(
                        onDigitOrDot = onDigitOrDot,
                        onOperator = onOperator,
                        onPercent = onPercent,
                        onToggleParenthesis = onToggleParenthesis,
                        onToggleSign = onToggleSign,
                        onBackspace = onBackspace,
                        onAllClear = onAllClear,
                        onEquals = onEquals
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorDisplayCard(
    uiState: BasicCalcUiState,
    onCopyResult: () -> Unit,
    onSendToGst: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = uiState.expression,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("basic_calc_expression")
            )

            Text(
                text = uiState.errorText ?: "= ${uiState.liveResult}",
                style = if (uiState.errorText != null) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.displayMedium
                },
                color = if (uiState.errorText != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("basic_calc_result")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyResult,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("basic_calc_copy")
                ) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy")
                }
                Button(
                    onClick = onSendToGst,
                    enabled = uiState.latestNumericValue != null &&
                        uiState.latestNumericValue > BigDecimal.ZERO,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1.35f)
                        .height(44.dp)
                        .testTag("basic_calc_send_to_gst")
                ) {
                    Text("Use in GST")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BasicCalcHistoryStrip(
    history: List<BasicCalcHistoryItem>,
    onUseItem: (BasicCalcHistoryItem) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Calculations",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (history.isNotEmpty()) {
                TextButton(
                    onClick = onClearHistory,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Rounded.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        if (history.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                history.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onUseItem(item) }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = item.expression,
                                style = FormulaTextStyle,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "= ${item.resultFormatted}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorKeypad(
    modifier: Modifier = Modifier,
    onDigitOrDot: (String) -> Unit,
    onOperator: (String) -> Unit,
    onPercent: () -> Unit,
    onToggleParenthesis: () -> Unit,
    onToggleSign: () -> Unit,
    onBackspace: () -> Unit,
    onAllClear: () -> Unit,
    onEquals: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: AC, ( ), %, ⌫
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcKeyButton(
                label = "AC",
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                onClick = onAllClear,
                modifier = Modifier
                    .weight(1f)
                    .testTag("key_ac")
            )
            CalcKeyButton(
                label = "( )",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onToggleParenthesis,
                modifier = Modifier
                    .weight(1f)
                    .testTag("key_paren")
            )
            CalcKeyButton(
                label = "%",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onPercent,
                modifier = Modifier
                    .weight(1f)
                    .testTag("key_percent")
            )
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onBackspace)
                    .testTag("key_backspace")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Backspace,
                        contentDescription = "Backspace",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // Row 2: 7, 8, 9, ÷
        KeypadRow(
            keys = listOf("7", "8", "9"),
            operator = "÷",
            onDigitOrDot = onDigitOrDot,
            onOperator = onOperator
        )
        // Row 3: 4, 5, 6, ×
        KeypadRow(
            keys = listOf("4", "5", "6"),
            operator = "×",
            onDigitOrDot = onDigitOrDot,
            onOperator = onOperator
        )
        // Row 4: 1, 2, 3, −
        KeypadRow(
            keys = listOf("1", "2", "3"),
            operator = "−",
            onDigitOrDot = onDigitOrDot,
            onOperator = onOperator
        )

        // Row 5: ±, 0, ., + / =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalcKeyButton(
                label = "±",
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                onClick = onToggleSign,
                modifier = Modifier
                    .weight(1f)
                    .testTag("key_sign")
            )
            CalcKeyButton(
                label = "0",
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                onClick = { onDigitOrDot("0") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("key_0")
            )
            CalcKeyButton(
                label = ".",
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                onClick = { onDigitOrDot(".") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("key_dot")
            )
            CalcKeyButton(
                label = "+",
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { onOperator("+") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("key_plus")
            )
        }

        // Full-width crisp Equals Bar
        Button(
            onClick = onEquals,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("key_equals")
        ) {
            Text(
                text = "=  Calculate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun KeypadRow(
    keys: List<String>,
    operator: String,
    onDigitOrDot: (String) -> Unit,
    onOperator: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        keys.forEach { digit ->
            CalcKeyButton(
                label = digit,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                onClick = { onDigitOrDot(digit) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("key_$digit")
            )
        }
        CalcKeyButton(
            label = operator,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            onClick = { onOperator(operator) },
            modifier = Modifier
                .weight(1f)
                .testTag("key_op_$operator")
        )
    }
}

@Composable
private fun CalcKeyButton(
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = border,
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
