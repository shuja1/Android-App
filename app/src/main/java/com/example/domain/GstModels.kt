package com.example.domain

import java.math.BigDecimal

enum class TaxMode(val label: String, val shortLabel: String) {
    EXCLUSIVE("Exclusive of GST (+ GST)", "Add GST"),
    INCLUSIVE("Inclusive of GST (- GST)", "Remove GST")
}

enum class SupplyType(val label: String, val shortLabel: String) {
    INTRA_STATE("Intra-State (CGST + SGST)", "CGST + SGST"),
    INTER_STATE("Inter-State (IGST)", "IGST")
}

enum class CurrencyOption(val symbol: String, val code: String, val displayName: String) {
    INR("₹", "INR", "Indian Rupee"),
    USD("$", "USD", "US Dollar"),
    EUR("€", "EUR", "Euro"),
    GBP("£", "GBP", "British Pound"),
    AED("AED ", "AED", "UAE Dirham"),
    SGD("S$", "SGD", "Singapore Dollar"),
    AUD("A$", "AUD", "Australian Dollar"),
    CAD("C$", "CAD", "Canadian Dollar"),
    JPY("¥", "JPY", "Japanese Yen"),
    SAR("SAR ", "SAR", "Saudi Riyal")
}

data class HsnProductItem(
    val hsnCode: String,
    val productName: String,
    val category: String,
    val gstRate: BigDecimal,
    val updateNote: String,
    val isServiceSac: Boolean = false
)

data class GstSlabInfo(
    val rate: BigDecimal,
    val displayRate: String,
    val categoryTitle: String,
    val examples: String,
    val note: String
)

data class GstStep(
    val stepNumber: Int,
    val title: String,
    val formula: String,
    val calculation: String,
    val resultFormatted: String
)

data class SlabComparisonItem(
    val rateDisplay: String,
    val rateValue: BigDecimal,
    val baseAmount: BigDecimal,
    val gstAmount: BigDecimal,
    val totalAmount: BigDecimal
)

data class GstCalculationInput(
    val amount: BigDecimal,
    val gstRatePercent: BigDecimal,
    val cessRatePercent: BigDecimal = BigDecimal.ZERO,
    val quantity: Int = 1,
    val discountPercent: BigDecimal = BigDecimal.ZERO,
    val taxMode: TaxMode = TaxMode.EXCLUSIVE,
    val supplyType: SupplyType = SupplyType.INTRA_STATE,
    val currencySymbol: String = "₹"
)

data class GstCalculationResult(
    val input: GstCalculationInput,
    val grossInputTotal: BigDecimal,
    val discountAmount: BigDecimal,
    val netTaxableAmount: BigDecimal,
    val cgstRatePercent: BigDecimal,
    val sgstRatePercent: BigDecimal,
    val igstRatePercent: BigDecimal,
    val cgstAmount: BigDecimal,
    val sgstAmount: BigDecimal,
    val igstAmount: BigDecimal,
    val cessAmount: BigDecimal,
    val totalGstAmount: BigDecimal,
    val totalTaxWithCess: BigDecimal,
    val grandTotalAmount: BigDecimal,
    val effectiveTaxSharePercent: BigDecimal,
    val steps: List<GstStep>,
    val slabComparison: List<SlabComparisonItem>
)

data class SavedGstCalculation(
    val id: String,
    val title: String,
    val partyName: String,
    val hsnCode: String,
    val timestampMillis: Long,
    val amount: String,
    val gstRatePercent: String,
    val cessRatePercent: String,
    val quantity: Int,
    val discountPercent: String,
    val taxMode: TaxMode,
    val supplyType: SupplyType,
    val currencySymbol: String,
    val netTaxableFormatted: String,
    val totalTaxFormatted: String,
    val grandTotalFormatted: String
)

data class BusinessProfile(
    val businessName: String = "",
    val gstin: String = "",
    val addressLine: String = ""
)

data class PdfReportMetadata(
    val reportTitle: String = "GST Tax Calculation Report",
    val partyName: String = "",
    val hsnCode: String = "",
    val notes: String = "",
    val businessProfile: BusinessProfile = BusinessProfile()
)
