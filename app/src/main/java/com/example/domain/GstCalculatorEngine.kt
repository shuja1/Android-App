package com.example.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object GstCalculatorEngine {

    val STANDARD_SLABS: List<BigDecimal> = listOf(
        BigDecimal("0"),
        BigDecimal("0.25"),
        BigDecimal("1"),
        BigDecimal("3"),
        BigDecimal("5"),
        BigDecimal("12"),
        BigDecimal("18"),
        BigDecimal("28"),
        BigDecimal("40")
    )

    val SLAB_GUIDE_ITEMS: List<GstSlabInfo> = listOf(
        GstSlabInfo(
            rate = BigDecimal("0"),
            displayRate = "0%",
            categoryTitle = "Exempt & Essential Commodities",
            examples = "Fresh fruits & vegetables, unpacked food grains, milk, curd, salt, books, newspapers, basic healthcare & education.",
            note = "Nil-rated supplies carry zero output GST liability."
        ),
        GstSlabInfo(
            rate = BigDecimal("0.25"),
            displayRate = "0.25%",
            categoryTitle = "Rough Precious & Semi-Precious Stones",
            examples = "Uncut and unpolished diamonds, rough industrial diamonds, synthetic or reconstructed semi-precious stones.",
            note = "Special micro-rate slab (0.125% CGST + 0.125% SGST)."
        ),
        GstSlabInfo(
            rate = BigDecimal("1"),
            displayRate = "1%",
            categoryTitle = "Composition Scheme & Affordable Housing",
            examples = "Composition levy traders/manufacturers (1% turnover tax), affordable under-construction housing units without ITC.",
            note = "Splits into 0.5% CGST + 0.5% SGST for intra-state."
        ),
        GstSlabInfo(
            rate = BigDecimal("3"),
            displayRate = "3%",
            categoryTitle = "Gold, Silver & Jewellery",
            examples = "Gold bars, silver bullion, platinum ornaments, pearls, precious metal coins and crafted jewellery.",
            note = "Splits into 1.5% CGST + 1.5% SGST for intra-state."
        ),
        GstSlabInfo(
            rate = BigDecimal("5"),
            displayRate = "5%",
            categoryTitle = "Daily Essentials & Transport",
            examples = "Packaged staples, edible oils, tea, coffee, spices, economy rail/air travel, small eateries, life-saving medicines.",
            note = "Splits into 2.5% CGST + 2.5% SGST for intra-state."
        ),
        GstSlabInfo(
            rate = BigDecimal("12"),
            displayRate = "12%",
            categoryTitle = "Processed Foods & Standard Goods",
            examples = "Butter, ghee, cheese, fruit juices, apparel above threshold, mobile accessories, business-class air travel, construction works.",
            note = "Splits into 6% CGST + 6% SGST for intra-state."
        ),
        GstSlabInfo(
            rate = BigDecimal("18"),
            displayRate = "18%",
            categoryTitle = "Standard Services, IT & Industrial Goods",
            examples = "Software & SaaS, consulting, telecom, restaurants in hotels, electronics, laptops, monitors, capital goods, banking services.",
            note = "Most widely used GST slab (9% CGST + 9% SGST)."
        ),
        GstSlabInfo(
            rate = BigDecimal("28"),
            displayRate = "28%",
            categoryTitle = "Automobiles, Luxury & Sin Goods",
            examples = "Passenger cars, two-wheelers above 350cc, air conditioners, refrigerators, cement, luxury hotels, aerated beverages.",
            note = "Splits into 14% CGST + 14% SGST for intra-state."
        ),
        GstSlabInfo(
            rate = BigDecimal("40"),
            displayRate = "40%",
            categoryTitle = "Special Luxury / Demerit / Sin Rate",
            examples = "High-end luxury vehicles, aerated/caffeinated beverages, tobacco products, and statutory ceiling rate / GST + Cess combined slab.",
            note = "Splits into 20% CGST + 20% SGST for intra-state."
        )
    )

    private val HUNDRED = BigDecimal("100")
    private val TWO = BigDecimal("2")

    fun calculate(input: GstCalculationInput): GstCalculationResult {
        val safeAmount = input.amount.max(BigDecimal.ZERO)
        val safeQty = input.quantity.coerceAtLeast(1)
        val safeGstRate = input.gstRatePercent.max(BigDecimal.ZERO).min(HUNDRED)
        val safeCessRate = input.cessRatePercent.max(BigDecimal.ZERO).min(HUNDRED)
        val safeDiscountRate = input.discountPercent.max(BigDecimal.ZERO).min(HUNDRED)

        val rawGross = safeAmount.multiply(BigDecimal(safeQty))
        val discountAmt = if (safeDiscountRate > BigDecimal.ZERO) {
            rawGross.multiply(safeDiscountRate).divide(HUNDRED, 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        }
        val afterDiscountInput = rawGross.subtract(discountAmt).max(BigDecimal.ZERO)

        val totalCombinedRate = safeGstRate.add(safeCessRate)

        val netTaxable: BigDecimal
        val totalGst: BigDecimal
        val cessAmt: BigDecimal
        val grandTotal: BigDecimal

        if (input.taxMode == TaxMode.EXCLUSIVE) {
            netTaxable = afterDiscountInput.setScale(2, RoundingMode.HALF_UP)
            totalGst = netTaxable.multiply(safeGstRate).divide(HUNDRED, 2, RoundingMode.HALF_UP)
            cessAmt = if (safeCessRate > BigDecimal.ZERO) {
                netTaxable.multiply(safeCessRate).divide(HUNDRED, 2, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            }
            grandTotal = netTaxable.add(totalGst).add(cessAmt).setScale(2, RoundingMode.HALF_UP)
        } else {
            grandTotal = afterDiscountInput.setScale(2, RoundingMode.HALF_UP)
            val divisor = HUNDRED.add(totalCombinedRate)
            netTaxable = if (divisor > BigDecimal.ZERO) {
                grandTotal.multiply(HUNDRED).divide(divisor, 2, RoundingMode.HALF_UP)
            } else {
                grandTotal
            }
            val combinedTax = grandTotal.subtract(netTaxable).max(BigDecimal.ZERO)
            if (safeCessRate > BigDecimal.ZERO && totalCombinedRate > BigDecimal.ZERO) {
                cessAmt = netTaxable.multiply(safeCessRate).divide(HUNDRED, 2, RoundingMode.HALF_UP)
                totalGst = combinedTax.subtract(cessAmt).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)
            } else {
                cessAmt = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                totalGst = combinedTax.setScale(2, RoundingMode.HALF_UP)
            }
        }

        val cgstRate: BigDecimal
        val sgstRate: BigDecimal
        val igstRate: BigDecimal
        val cgstAmt: BigDecimal
        val sgstAmt: BigDecimal
        val igstAmt: BigDecimal

        if (input.supplyType == SupplyType.INTRA_STATE) {
            cgstRate = safeGstRate.divide(TWO, 4, RoundingMode.HALF_UP).stripTrailingZeros()
            sgstRate = cgstRate
            igstRate = BigDecimal.ZERO
            cgstAmt = totalGst.divide(TWO, 2, RoundingMode.HALF_UP)
            sgstAmt = totalGst.subtract(cgstAmt).setScale(2, RoundingMode.HALF_UP)
            igstAmt = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        } else {
            cgstRate = BigDecimal.ZERO
            sgstRate = BigDecimal.ZERO
            igstRate = safeGstRate.stripTrailingZeros()
            cgstAmt = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            sgstAmt = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            igstAmt = totalGst
        }

        val totalTaxWithCess = totalGst.add(cessAmt).setScale(2, RoundingMode.HALF_UP)
        val effectiveTaxShare = if (grandTotal > BigDecimal.ZERO) {
            totalTaxWithCess.multiply(HUNDRED).divide(grandTotal, 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        }

        val steps = buildSteps(
            currency = input.currencySymbol,
            unitAmount = safeAmount,
            quantity = safeQty,
            rawGross = rawGross,
            discountRate = safeDiscountRate,
            discountAmt = discountAmt,
            afterDiscountInput = afterDiscountInput,
            gstRate = safeGstRate,
            cessRate = safeCessRate,
            taxMode = input.taxMode,
            supplyType = input.supplyType,
            netTaxable = netTaxable,
            totalGst = totalGst,
            cgstRate = cgstRate,
            sgstRate = sgstRate,
            cgstAmt = cgstAmt,
            sgstAmt = sgstAmt,
            igstAmt = igstAmt,
            cessAmt = cessAmt,
            grandTotal = grandTotal
        )

        val comparisons = STANDARD_SLABS.map { slabRate ->
            val slabBase: BigDecimal
            val slabTax: BigDecimal
            val slabTotal: BigDecimal
            if (input.taxMode == TaxMode.EXCLUSIVE) {
                slabBase = afterDiscountInput.setScale(2, RoundingMode.HALF_UP)
                slabTax = slabBase.multiply(slabRate).divide(HUNDRED, 2, RoundingMode.HALF_UP)
                slabTotal = slabBase.add(slabTax).setScale(2, RoundingMode.HALF_UP)
            } else {
                slabTotal = afterDiscountInput.setScale(2, RoundingMode.HALF_UP)
                val div = HUNDRED.add(slabRate)
                slabBase = slabTotal.multiply(HUNDRED).divide(div, 2, RoundingMode.HALF_UP)
                slabTax = slabTotal.subtract(slabBase).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)
            }
            SlabComparisonItem(
                rateDisplay = "${formatRate(slabRate)}%",
                rateValue = slabRate,
                baseAmount = slabBase,
                gstAmount = slabTax,
                totalAmount = slabTotal
            )
        }

        return GstCalculationResult(
            input = input.copy(
                amount = safeAmount,
                quantity = safeQty,
                gstRatePercent = safeGstRate,
                cessRatePercent = safeCessRate,
                discountPercent = safeDiscountRate
            ),
            grossInputTotal = rawGross.setScale(2, RoundingMode.HALF_UP),
            discountAmount = discountAmt,
            netTaxableAmount = netTaxable,
            cgstRatePercent = cgstRate,
            sgstRatePercent = sgstRate,
            igstRatePercent = igstRate,
            cgstAmount = cgstAmt,
            sgstAmount = sgstAmt,
            igstAmount = igstAmt,
            cessAmount = cessAmt,
            totalGstAmount = totalGst,
            totalTaxWithCess = totalTaxWithCess,
            grandTotalAmount = grandTotal,
            effectiveTaxSharePercent = effectiveTaxShare,
            steps = steps,
            slabComparison = comparisons
        )
    }

    private fun buildSteps(
        currency: String,
        unitAmount: BigDecimal,
        quantity: Int,
        rawGross: BigDecimal,
        discountRate: BigDecimal,
        discountAmt: BigDecimal,
        afterDiscountInput: BigDecimal,
        gstRate: BigDecimal,
        cessRate: BigDecimal,
        taxMode: TaxMode,
        supplyType: SupplyType,
        netTaxable: BigDecimal,
        totalGst: BigDecimal,
        cgstRate: BigDecimal,
        sgstRate: BigDecimal,
        cgstAmt: BigDecimal,
        sgstAmt: BigDecimal,
        igstAmt: BigDecimal,
        cessAmt: BigDecimal,
        grandTotal: BigDecimal
    ): List<GstStep> {
        val steps = mutableListOf<GstStep>()
        var stepIdx = 1

        // Step 1: Quantity & Discount preparation (only if qty > 1 or discount > 0, else concise initial amount step)
        if (quantity > 1 || discountRate > BigDecimal.ZERO) {
            val baseDesc = if (quantity > 1) {
                "${formatMoney(unitAmount, currency)} × $quantity units = ${formatMoney(rawGross, currency)}"
            } else {
                formatMoney(rawGross, currency)
            }
            val discountPart = if (discountRate > BigDecimal.ZERO) {
                " − ${formatRate(discountRate)}% discount (${formatMoney(discountAmt, currency)})"
            } else {
                ""
            }
            steps.add(
                GstStep(
                    stepNumber = stepIdx++,
                    title = if (taxMode == TaxMode.EXCLUSIVE) "Determine Net Base Input" else "Determine Gross Input After Adjustments",
                    formula = if (discountRate > BigDecimal.ZERO) "(Unit × Qty) − Discount" else "Unit Price × Quantity",
                    calculation = "$baseDesc$discountPart",
                    resultFormatted = formatMoney(afterDiscountInput, currency)
                )
            )
        } else {
            steps.add(
                GstStep(
                    stepNumber = stepIdx++,
                    title = if (taxMode == TaxMode.EXCLUSIVE) "Identify Base (Net) Amount" else "Identify Gross (Tax-Inclusive) Amount",
                    formula = if (taxMode == TaxMode.EXCLUSIVE) "Base Amount = Entered Value" else "Gross Total = Entered Value",
                    calculation = "Input amount before GST adjustments",
                    resultFormatted = formatMoney(afterDiscountInput, currency)
                )
            )
        }

        val combinedRate = gstRate.add(cessRate)

        if (taxMode == TaxMode.EXCLUSIVE) {
            // Step 2: Calculate GST
            steps.add(
                GstStep(
                    stepNumber = stepIdx++,
                    title = "Compute Total GST (${formatRate(gstRate)}%)",
                    formula = "GST = Base × (Rate ÷ 100)",
                    calculation = "${formatMoney(netTaxable, currency)} × (${formatRate(gstRate)} ÷ 100)",
                    resultFormatted = formatMoney(totalGst, currency)
                )
            )

            // Optional Cess step
            if (cessRate > BigDecimal.ZERO) {
                steps.add(
                    GstStep(
                        stepNumber = stepIdx++,
                        title = "Compute Compensation Cess (${formatRate(cessRate)}%)",
                        formula = "Cess = Base × (Cess Rate ÷ 100)",
                        calculation = "${formatMoney(netTaxable, currency)} × (${formatRate(cessRate)} ÷ 100)",
                        resultFormatted = formatMoney(cessAmt, currency)
                    )
                )
            }

            // Step 3: Split CGST+SGST or IGST
            if (supplyType == SupplyType.INTRA_STATE) {
                steps.add(
                    GstStep(
                        stepNumber = stepIdx++,
                        title = "Split Intra-State Tax (CGST + SGST)",
                        formula = "CGST = SGST = GST ÷ 2 (${formatRate(cgstRate)}% each)",
                        calculation = "${formatMoney(totalGst, currency)} ÷ 2 → CGST ${formatMoney(cgstAmt, currency)} + SGST ${formatMoney(sgstAmt, currency)}",
                        resultFormatted = "${formatMoney(cgstAmt, currency)} + ${formatMoney(sgstAmt, currency)}"
                    )
                )
            } else {
                steps.add(
                    GstStep(
                        stepNumber = stepIdx++,
                        title = "Assign Inter-State Tax (IGST)",
                        formula = "IGST = 100% of GST (${formatRate(gstRate)}%)",
                        calculation = "Single integrated tax component for inter-state supply",
                        resultFormatted = formatMoney(igstAmt, currency)
                    )
                )
            }

            // Step 4: Grand Total
            val addendCess = if (cessRate > BigDecimal.ZERO) " + ${formatMoney(cessAmt, currency)}" else ""
            steps.add(
                GstStep(
                    stepNumber = stepIdx,
                    title = "Calculate Gross Invoice Total",
                    formula = if (cessRate > BigDecimal.ZERO) "Total = Base + GST + Cess" else "Total = Base + GST",
                    calculation = "${formatMoney(netTaxable, currency)} + ${formatMoney(totalGst, currency)}$addendCess",
                    resultFormatted = formatMoney(grandTotal, currency)
                )
            )
        } else {
            // INCLUSIVE MODE
            val divisor = HUNDRED.add(combinedRate)
            steps.add(
                GstStep(
                    stepNumber = stepIdx++,
                    title = "Reverse-Calculate Net Base Amount",
                    formula = "Base = (Gross × 100) ÷ (100 + ${formatRate(combinedRate)})",
                    calculation = "(${formatMoney(grandTotal, currency)} × 100) ÷ ${formatRate(divisor)}",
                    resultFormatted = formatMoney(netTaxable, currency)
                )
            )

            if (cessRate > BigDecimal.ZERO) {
                steps.add(
                    GstStep(
                        stepNumber = stepIdx++,
                        title = "Extract Cess (${formatRate(cessRate)}%) & GST (${formatRate(gstRate)}%)",
                        formula = "Cess = Base × ${formatRate(cessRate)}% | GST = Gross − Base − Cess",
                        calculation = "Cess ${formatMoney(cessAmt, currency)}, GST ${formatMoney(totalGst, currency)}",
                        resultFormatted = formatMoney(totalGst.add(cessAmt), currency)
                    )
                )
            } else {
                steps.add(
                    GstStep(
                        stepNumber = stepIdx++,
                        title = "Extract Total GST Amount",
                        formula = "GST = Gross Total − Net Base",
                        calculation = "${formatMoney(grandTotal, currency)} − ${formatMoney(netTaxable, currency)}",
                        resultFormatted = formatMoney(totalGst, currency)
                    )
                )
            }

            if (supplyType == SupplyType.INTRA_STATE) {
                steps.add(
                    GstStep(
                        stepNumber = stepIdx,
                        title = "Split Intra-State Tax (CGST + SGST)",
                        formula = "CGST = SGST = GST ÷ 2 (${formatRate(cgstRate)}% each)",
                        calculation = "${formatMoney(totalGst, currency)} ÷ 2 → CGST ${formatMoney(cgstAmt, currency)} + SGST ${formatMoney(sgstAmt, currency)}",
                        resultFormatted = "${formatMoney(cgstAmt, currency)} + ${formatMoney(sgstAmt, currency)}"
                    )
                )
            } else {
                steps.add(
                    GstStep(
                        stepNumber = stepIdx,
                        title = "Assign Inter-State Tax (IGST)",
                        formula = "IGST = 100% of Extracted GST (${formatRate(gstRate)}%)",
                        calculation = "Single integrated tax component for inter-state supply",
                        resultFormatted = formatMoney(igstAmt, currency)
                    )
                )
            }
        }

        return steps
    }

    fun formatMoney(value: BigDecimal, currencySymbol: String = "₹"): String {
        val symbols = DecimalFormatSymbols(Locale("en", "IN"))
        val formatter = DecimalFormat("#,##,##0.00", symbols)
        val indianFormatted = if (currencySymbol == "₹") {
            formatIndianNumber(value.setScale(2, RoundingMode.HALF_UP))
        } else {
            val stdSymbols = DecimalFormatSymbols(Locale.US)
            DecimalFormat("#,##0.00", stdSymbols).format(value.setScale(2, RoundingMode.HALF_UP))
        }
        return "$currencySymbol$indianFormatted"
    }

    private fun formatIndianNumber(value: BigDecimal): String {
        val plain = value.abs().setScale(2, RoundingMode.HALF_UP).toPlainString()
        val parts = plain.split(".")
        val intPart = parts[0]
        val decPart = parts[1]
        val groupedInt = if (intPart.length <= 3) {
            intPart
        } else {
            val lastThree = intPart.takeLast(3)
            val rest = intPart.dropLast(3)
            val restGrouped = rest.reversed().chunked(2).joinToString(",").reversed()
            "$restGrouped,$lastThree"
        }
        val sign = if (value < BigDecimal.ZERO) "-" else ""
        return "$sign$groupedInt.$decPart"
    }

    fun formatRate(rate: BigDecimal): String {
        val stripped = rate.stripTrailingZeros()
        return if (stripped.scale() <= 0) stripped.toPlainString() else stripped.toPlainString()
    }
}
