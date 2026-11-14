package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.GstCalculationResult
import com.example.domain.GstCalculatorEngine
import com.example.domain.GstStep
import com.example.domain.SupplyType
import com.example.domain.TaxMode
import com.example.ui.CalculatorUiState
import com.example.ui.components.CurrencySelectorChip
import com.example.ui.components.ElemensysBrandHeader
import com.example.ui.theme.FormulaTextStyle
import com.example.ui.theme.HeroAccentGold
import com.example.ui.theme.HeroGradientEnd
import com.example.ui.theme.HeroGradientStart
import java.math.BigDecimal

@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    onAmountChange: (String) -> Unit,
    onAddQuickAmount: (Long) -> Unit,
    onClearAmount: () -> Unit,
    onSelectStandardRate: (BigDecimal) -> Unit,
    onSelectCustomRate: () -> Unit,
    onCustomRateChange: (String) -> Unit,
    onCessRateChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onDiscountChange: (String) -> Unit,
    onTaxModeChange: (TaxMode) -> Unit,
    onSupplyTypeChange: (SupplyType) -> Unit,
    onToggleAdvanced: () -> Unit,
    onToggleComparison: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onBrowseHsn: () -> Unit,
    onClearActiveHsn: () -> Unit,
    onSaveCalculation: () -> Unit,
    onCopySummary: () -> Unit,
    onOpenPdfExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ElemensysBrandHeader(
            subtitle = "All GST Slabs • Step-by-Step • PDF Export",
            trailingAction = {
                Button(
                    onClick = onOpenPdfExport,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("top_export_pdf_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PictureAsPdf,
                        contentDescription = "Export PDF Report",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF", style = MaterialTheme.typography.labelLarge)
                }
            }
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isWideScreen = maxWidth >= 760.dp
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CalculatorInputCard(
                            uiState = uiState,
                            onAmountChange = onAmountChange,
                            onAddQuickAmount = onAddQuickAmount,
                            onClearAmount = onClearAmount,
                            onSelectStandardRate = onSelectStandardRate,
                            onSelectCustomRate = onSelectCustomRate,
                            onCustomRateChange = onCustomRateChange,
                            onCessRateChange = onCessRateChange,
                            onQuantityChange = onQuantityChange,
                            onDiscountChange = onDiscountChange,
                            onTaxModeChange = onTaxModeChange,
                            onSupplyTypeChange = onSupplyTypeChange,
                            onToggleAdvanced = onToggleAdvanced,
                            onCurrencyChange = onCurrencyChange,
                            onBrowseHsn = onBrowseHsn,
                            onClearActiveHsn = onClearActiveHsn
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HeroLedgerResultCard(
                            result = uiState.result,
                            onCopySummary = onCopySummary,
                            onSaveCalculation = onSaveCalculation,
                            onOpenPdfExport = onOpenPdfExport
                        )
                        StepByStepBreakdownCard(result = uiState.result)
                        AllSlabsComparisonCard(
                            result = uiState.result,
                            expanded = uiState.showAllSlabsComparison,
                            onToggle = onToggleComparison,
                            onSelectRate = onSelectStandardRate
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HeroLedgerResultCard(
                        result = uiState.result,
                        onCopySummary = onCopySummary,
                        onSaveCalculation = onSaveCalculation,
                        onOpenPdfExport = onOpenPdfExport
                    )
                    CalculatorInputCard(
                        uiState = uiState,
                        onAmountChange = onAmountChange,
                        onAddQuickAmount = onAddQuickAmount,
                        onClearAmount = onClearAmount,
                        onSelectStandardRate = onSelectStandardRate,
                        onSelectCustomRate = onSelectCustomRate,
                        onCustomRateChange = onCustomRateChange,
                        onCessRateChange = onCessRateChange,
                        onQuantityChange = onQuantityChange,
                        onDiscountChange = onDiscountChange,
                        onTaxModeChange = onTaxModeChange,
                        onSupplyTypeChange = onSupplyTypeChange,
                        onToggleAdvanced = onToggleAdvanced,
                        onCurrencyChange = onCurrencyChange,
                        onBrowseHsn = onBrowseHsn,
                        onClearActiveHsn = onClearActiveHsn
                    )
                    StepByStepBreakdownCard(result = uiState.result)
                    AllSlabsComparisonCard(
                        result = uiState.result,
                        expanded = uiState.showAllSlabsComparison,
                        onToggle = onToggleComparison,
                        onSelectRate = onSelectStandardRate
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun HeroLedgerResultCard(
    result: GstCalculationResult,
    onCopySummary: () -> Unit,
    onSaveCalculation: () -> Unit,
    onOpenPdfExport: () -> Unit
) {
    val currency = result.input.currencySymbol
    val rateStr = GstCalculatorEngine.formatRate(result.input.gstRatePercent)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(HeroGradientStart, HeroGradientEnd)
                )
            )
            .drawBehind {
                // Subtle ledger geometry accent lines
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = size.width * 0.35f,
                    center = Offset(size.width * 0.92f, size.height * 0.12f)
                )
                drawCircle(
                    color = HeroAccentGold.copy(alpha = 0.10f),
                    radius = size.width * 0.18f,
                    center = Offset(size.width * 0.08f, size.height * 0.95f)
                )
            }
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "${result.input.taxMode.shortLabel.uppercase()} • $rateStr% GST",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = result.input.supplyType.shortLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFC8F7E4)
                )
            }

            Column {
                Text(
                    text = "Gross Invoice Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFC8F7E4)
                )
                Text(
                    text = GstCalculatorEngine.formatMoney(result.grandTotalAmount, currency),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("hero_grand_total_text")
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.16f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Net Base Amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFC8F7E4)
                    )
                    Text(
                        text = GstCalculatorEngine.formatMoney(result.netTaxableAmount, currency),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.testTag("hero_net_base_text")
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total GST + Cess",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFDE68A)
                    )
                    Text(
                        text = GstCalculatorEngine.formatMoney(result.totalTaxWithCess, currency),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFDE68A),
                        modifier = Modifier.testTag("hero_total_tax_text")
                    )
                }
            }

            // Tax component chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (result.input.supplyType == SupplyType.INTRA_STATE) {
                    TaxSplitPill(
                        label = "CGST (${GstCalculatorEngine.formatRate(result.cgstRatePercent)}%)",
                        amount = GstCalculatorEngine.formatMoney(result.cgstAmount, currency)
                    )
                    TaxSplitPill(
                        label = "SGST (${GstCalculatorEngine.formatRate(result.sgstRatePercent)}%)",
                        amount = GstCalculatorEngine.formatMoney(result.sgstAmount, currency)
                    )
                } else {
                    TaxSplitPill(
                        label = "IGST (${GstCalculatorEngine.formatRate(result.igstRatePercent)}%)",
                        amount = GstCalculatorEngine.formatMoney(result.igstAmount, currency)
                    )
                }
                if (result.cessAmount > BigDecimal.ZERO) {
                    TaxSplitPill(
                        label = "Cess (${GstCalculatorEngine.formatRate(result.input.cessRatePercent)}%)",
                        amount = GstCalculatorEngine.formatMoney(result.cessAmount, currency)
                    )
                }
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopySummary,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("copy_summary_button")
                ) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy", style = MaterialTheme.typography.labelLarge)
                }
                OutlinedButton(
                    onClick = onSaveCalculation,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("save_calculation_button")
                ) {
                    Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save", style = MaterialTheme.typography.labelLarge)
                }
                Button(
                    onClick = onOpenPdfExport,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = HeroGradientStart
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1.25f)
                        .height(44.dp)
                        .testTag("hero_export_pdf_button")
                ) {
                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export PDF", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun TaxSplitPill(label: String, amount: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFC8F7E4)
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CalculatorInputCard(
    uiState: CalculatorUiState,
    onAmountChange: (String) -> Unit,
    onAddQuickAmount: (Long) -> Unit,
    onClearAmount: () -> Unit,
    onSelectStandardRate: (BigDecimal) -> Unit,
    onSelectCustomRate: () -> Unit,
    onCustomRateChange: (String) -> Unit,
    onCessRateChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onDiscountChange: (String) -> Unit,
    onTaxModeChange: (TaxMode) -> Unit,
    onSupplyTypeChange: (SupplyType) -> Unit,
    onToggleAdvanced: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onBrowseHsn: () -> Unit,
    onClearActiveHsn: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Amount Input + Currency Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.taxMode == TaxMode.EXCLUSIVE) {
                            "Base Amount (Before GST)"
                        } else {
                            "Total Amount (Including GST)"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    CurrencySelectorChip(
                        selectedSymbol = uiState.currencySymbol,
                        onCurrencySelected = onCurrencyChange,
                        testTag = "calculator_currency_selector"
                    )
                }

                OutlinedTextField(
                    value = uiState.amountText,
                    onValueChange = onAmountChange,
                    prefix = {
                        Text(
                            text = uiState.currencySymbol,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (uiState.amountText.isNotEmpty()) {
                            IconButton(
                                onClick = onClearAmount,
                                modifier = Modifier.testTag("clear_amount_button")
                            ) {
                                Icon(Icons.Rounded.Clear, contentDescription = "Clear amount")
                            }
                        }
                    },
                    textStyle = MaterialTheme.typography.headlineMedium,
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input_field")
                )

                // Quick Increment Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100L, 500L, 1000L, 5000L, 10000L).forEach { delta ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onAddQuickAmount(delta) }
                                .testTag("quick_add_$delta")
                        ) {
                            Text(
                                text = "+$delta",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Tax Mode Segmented Control
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Calculation Mode",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    TaxMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = uiState.taxMode == mode,
                            onClick = { onTaxModeChange(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = TaxMode.entries.size
                            ),
                            modifier = Modifier.testTag("tax_mode_${mode.name.lowercase()}")
                        ) {
                            Text(mode.shortLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            // Supply Type Segmented Control
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Supply / Tax Split Type",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SupplyType.entries.forEachIndexed { index, supply ->
                        SegmentedButton(
                            selected = uiState.supplyType == supply,
                            onClick = { onSupplyTypeChange(supply) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = SupplyType.entries.size
                            ),
                            modifier = Modifier.testTag("supply_type_${supply.name.lowercase()}")
                        ) {
                            Text(supply.shortLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            // All GST Percentages Grid + Browse HSN shortcut
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select GST Percentage",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = onBrowseHsn,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("browse_hsn_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.QrCode2,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("HSN Rates", style = MaterialTheme.typography.labelLarge)
                    }
                }

                if (uiState.activeHsnCode.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "HSN ${uiState.activeHsnCode} • ${uiState.activeProductTitle}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = onClearActiveHsn,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear active HSN",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GstCalculatorEngine.STANDARD_SLABS.forEach { rate ->
                        val rateLabel = GstCalculatorEngine.formatRate(rate)
                        val isSelected = !uiState.isCustomRateSelected &&
                            uiState.selectedPresetRate?.compareTo(rate) == 0
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectStandardRate(rate) },
                            label = {
                                Text(
                                    text = "$rateLabel%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("gst_rate_chip_$rateLabel")
                        )
                    }
                    FilterChip(
                        selected = uiState.isCustomRateSelected,
                        onClick = onSelectCustomRate,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Percent,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Custom",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier.testTag("gst_rate_chip_custom")
                    )
                }

                AnimatedVisibility(
                    visible = uiState.isCustomRateSelected,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = uiState.customRateText,
                        onValueChange = onCustomRateChange,
                        label = { Text("Custom GST Rate (%)") },
                        suffix = { Text("%") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("custom_gst_rate_input")
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Expandable Quantity, Discount & Cess
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onToggleAdvanced)
                    .padding(vertical = 6.dp)
                    .testTag("toggle_advanced_options"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Quantity, Discount & Cess %",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (uiState.showAdvancedOptions) {
                        Icons.Rounded.ExpandLess
                    } else {
                        Icons.Rounded.ExpandMore
                    },
                    contentDescription = "Toggle advanced fields",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = uiState.showAdvancedOptions,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.quantityText,
                        onValueChange = onQuantityChange,
                        label = { Text("Qty") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.8f)
                            .testTag("quantity_input")
                    )
                    OutlinedTextField(
                        value = uiState.discountText,
                        onValueChange = onDiscountChange,
                        label = { Text("Disc %") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("discount_input")
                    )
                    OutlinedTextField(
                        value = uiState.cessRateText,
                        onValueChange = onCessRateChange,
                        label = { Text("Cess %") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cess_input")
                    )
                }
            }
        }
    }
}

@Composable
private fun StepByStepBreakdownCard(result: GstCalculationResult) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Calculate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Step-by-Step Calculation",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Verified formulas and substituted tax math",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            result.steps.forEach { step ->
                StepItemRow(step = step)
            }
        }
    }
}

@Composable
private fun StepItemRow(step: GstStep) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = step.resultFormatted,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
            ) {
                Text(
                    text = step.formula,
                    style = FormulaTextStyle,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Text(
                text = step.calculation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AllSlabsComparisonCard(
    result: GstCalculationResult,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelectRate: (BigDecimal) -> Unit
) {
    val currency = result.input.currencySymbol
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onToggle)
                    .padding(vertical = 4.dp)
                    .testTag("toggle_all_slabs_comparison"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CompareArrows,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Compare All GST Percentages",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Tap any row to switch rate (0% to 40%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                TextButton(onClick = onToggle) {
                    Text(if (expanded) "Hide" else "View All")
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Text("Rate", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.65f))
                        Text("Base", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text("GST", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                        Text("Total", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1.05f), textAlign = TextAlign.End)
                    }

                    result.slabComparison.forEach { item ->
                        val isSelected = item.rateValue.compareTo(result.input.gstRatePercent) == 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                                .clickable { onSelectRate(item.rateValue) }
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.rateDisplay,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.65f)
                            )
                            Text(
                                text = GstCalculatorEngine.formatMoney(item.baseAmount, currency),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = GstCalculatorEngine.formatMoney(item.gstAmount, currency),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(0.9f),
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = GstCalculatorEngine.formatMoney(item.totalAmount, currency),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1.05f),
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
