package com.example

import com.example.domain.GstCalculationInput
import com.example.domain.GstCalculatorEngine
import com.example.domain.SupplyType
import com.example.domain.TaxMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class GstCalculatorEngineTest {

    @Test
    fun exclusive_18Percent_intraState_splitsEquallyWithSteps() {
        val result = GstCalculatorEngine.calculate(
            GstCalculationInput(
                amount = BigDecimal("10000"),
                gstRatePercent = BigDecimal("18"),
                taxMode = TaxMode.EXCLUSIVE,
                supplyType = SupplyType.INTRA_STATE
            )
        )

        assertEquals(BigDecimal("10000.00"), result.netTaxableAmount)
        assertEquals(BigDecimal("1800.00"), result.totalGstAmount)
        assertEquals(BigDecimal("900.00"), result.cgstAmount)
        assertEquals(BigDecimal("900.00"), result.sgstAmount)
        assertEquals(BigDecimal("0.00"), result.igstAmount)
        assertEquals(BigDecimal("11800.00"), result.grandTotalAmount)
        assertEquals(4, result.steps.size)
    }

    @Test
    fun inclusive_18Percent_reverseCalculatesBase() {
        val result = GstCalculatorEngine.calculate(
            GstCalculationInput(
                amount = BigDecimal("11800"),
                gstRatePercent = BigDecimal("18"),
                taxMode = TaxMode.INCLUSIVE,
                supplyType = SupplyType.INTER_STATE
            )
        )

        assertEquals(BigDecimal("10000.00"), result.netTaxableAmount)
        assertEquals(BigDecimal("1800.00"), result.totalGstAmount)
        assertEquals(BigDecimal("1800.00"), result.igstAmount)
        assertEquals(BigDecimal("11800.00"), result.grandTotalAmount)
    }

    @Test
    fun allRequestedRatesPresentIncluding1And40() {
        val rates = GstCalculatorEngine.STANDARD_SLABS.map { GstCalculatorEngine.formatRate(it) }
        assertTrue(rates.containsAll(listOf("0", "0.25", "1", "3", "5", "12", "18", "28", "40")))
    }

    @Test
    fun exclusive_40Percent_withQuantityAndDiscount() {
        val result = GstCalculatorEngine.calculate(
            GstCalculationInput(
                amount = BigDecimal("1000"),
                quantity = 2,
                discountPercent = BigDecimal("10"),
                gstRatePercent = BigDecimal("40"),
                taxMode = TaxMode.EXCLUSIVE,
                supplyType = SupplyType.INTRA_STATE
            )
        )

        // 2000 - 10% = 1800 base; 40% GST = 720 (360 CGST + 360 SGST); Total = 2520
        assertEquals(BigDecimal("1800.00"), result.netTaxableAmount)
        assertEquals(BigDecimal("720.00"), result.totalGstAmount)
        assertEquals(BigDecimal("360.00"), result.cgstAmount)
        assertEquals(BigDecimal("360.00"), result.sgstAmount)
        assertEquals(BigDecimal("2520.00"), result.grandTotalAmount)
    }

    @Test
    fun currencyFormattingAndHsnDirectoryCoverage() {
        assertEquals("₹1,00,000.00", GstCalculatorEngine.formatMoney(BigDecimal("100000"), "₹"))
        assertEquals("$100,000.00", GstCalculatorEngine.formatMoney(BigDecimal("100000"), "$"))
        assertEquals("AED 100,000.00", GstCalculatorEngine.formatMoney(BigDecimal("100000"), "AED "))

        val hsnRates = com.example.data.HsnDirectoryData.items.map { GstCalculatorEngine.formatRate(it.gstRate) }.toSet()
        assertTrue(hsnRates.containsAll(listOf("0", "0.25", "1", "3", "5", "12", "18", "28", "40")))

        val rateSearch = com.example.data.HsnDirectoryData.filter(query = "12%")
        assertTrue(rateSearch.isNotEmpty())
        assertTrue(rateSearch.all { GstCalculatorEngine.formatRate(it.gstRate) == "12" })

        val categorySearch = com.example.data.HsnDirectoryData.filter(query = "", categoryFilter = "Electronics & IT")
        assertTrue(categorySearch.any { it.hsnCode == "8517" })
    }
}
