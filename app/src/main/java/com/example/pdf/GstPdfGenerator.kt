package com.example.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.R
import com.example.domain.GstCalculationResult
import com.example.domain.GstCalculatorEngine
import com.example.domain.PdfReportMetadata
import com.example.domain.SupplyType
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GstPdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 34f
    private const val CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN

    fun createShareablePdfUri(
        context: Context,
        result: GstCalculationResult,
        metadata: PdfReportMetadata
    ): Uri {
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val fileStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val pdfFile = File(reportsDir, "Elemensys_GST_Report_$fileStamp.pdf")
        FileOutputStream(pdfFile).use { out ->
            writePdfToOutputStream(context, result, metadata, out)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
    }

    fun writePdfToOutputStream(
        context: Context,
        result: GstCalculationResult,
        metadata: PdfReportMetadata,
        outputStream: OutputStream
    ) {
        val document = PdfDocument()
        val emeraldDark = Color.rgb(7, 89, 63)
        val emeraldLight = Color.rgb(232, 246, 240)
        val slateText = Color.rgb(17, 28, 24)
        val mutedText = Color.rgb(75, 92, 85)
        val borderGray = Color.rgb(210, 221, 216)
        val amberAccent = Color.rgb(180, 83, 9)
        val rowAlt = Color.rgb(246, 249, 247)

        val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            color = slateText
        }
        val regularPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            color = slateText
        }
        val monoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            color = emeraldDark
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = borderGray
        }

        var pageNum = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create())
        var canvas = page.canvas

        fun ensureSpace(neededHeight: Float, currentY: Float): Float {
            if (currentY + neededHeight <= PAGE_HEIGHT - 44f) return currentY
            drawFooter(canvas, regularPaint, mutedText, pageNum)
            document.finishPage(page)
            pageNum++
            page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create())
            canvas = page.canvas
            return MARGIN + 10f
        }

        // 1. Header Banner
        fillPaint.color = emeraldDark
        canvas.drawRoundRect(
            RectF(MARGIN, MARGIN, PAGE_WIDTH - MARGIN, MARGIN + 78f),
            10f, 10f, fillPaint
        )

        // Draw Elemensys wordmark logo inside crisp white pill badge
        val rawLogo = BitmapFactory.decodeResource(context.resources, R.drawable.elemensys_wordmark)
        if (rawLogo != null) {
            val badgeRect = RectF(MARGIN + 12f, MARGIN + 14f, MARGIN + 150f, MARGIN + 64f)
            fillPaint.color = Color.WHITE
            canvas.drawRoundRect(badgeRect, 8f, 8f, fillPaint)
            val scaled = Bitmap.createScaledBitmap(rawLogo, 126, 37, true)
            canvas.drawBitmap(scaled, MARGIN + 18f, MARGIN + 20f, null)
            if (scaled !== rawLogo) {
                scaled.recycle()
            }
            rawLogo.recycle()
        }

        val titlePaint = Paint(boldPaint).apply {
            color = Color.WHITE
            textSize = 14f
        }
        val subPaint = Paint(regularPaint).apply {
            color = Color.rgb(209, 245, 228)
            textSize = 9.5f
        }
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val reportTitle = metadata.reportTitle.ifBlank { "GST Tax Calculation Report" }
        canvas.drawText(reportTitle.take(36), MARGIN + 166f, MARGIN + 33f, titlePaint)
        canvas.drawText("Elemensys GST Calculator • Generated $dateStr", MARGIN + 166f, MARGIN + 50f, subPaint)
        canvas.drawText(
            "Mode: ${result.input.taxMode.shortLabel}  |  Supply: ${result.input.supplyType.shortLabel}",
            MARGIN + 166f,
            MARGIN + 64f,
            subPaint
        )

        var y = MARGIN + 94f

        // 2. Optional Business & Party Metadata Block
        val profile = metadata.businessProfile
        val hasMeta = profile.businessName.isNotBlank() ||
            profile.gstin.isNotBlank() ||
            metadata.partyName.isNotBlank() ||
            metadata.hsnCode.isNotBlank() ||
            metadata.notes.isNotBlank()

        if (hasMeta) {
            val lines = buildList {
                if (profile.businessName.isNotBlank()) add("Business: ${profile.businessName}")
                if (profile.gstin.isNotBlank()) add("GSTIN: ${profile.gstin}")
                if (profile.addressLine.isNotBlank()) add("Address: ${profile.addressLine}")
                if (metadata.partyName.isNotBlank()) add("Party / Client: ${metadata.partyName}")
                if (metadata.hsnCode.isNotBlank()) add("HSN / SAC Code: ${metadata.hsnCode}")
                if (metadata.notes.isNotBlank()) add("Notes: ${metadata.notes}")
            }
            val blockHeight = 20f + lines.size * 15f
            y = ensureSpace(blockHeight + 10f, y)
            fillPaint.color = rowAlt
            canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + blockHeight), 8f, 8f, fillPaint)
            canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + blockHeight), 8f, 8f, strokePaint)
            var ly = y + 18f
            regularPaint.textSize = 9.5f
            regularPaint.color = slateText
            lines.forEach { line ->
                canvas.drawText(line.take(92), MARGIN + 12f, ly, regularPaint)
                ly += 15f
            }
            y += blockHeight + 12f
        }

        // 3. Executive Summary Cards
        y = ensureSpace(96f, y)
        val currency = result.input.currencySymbol
        val cardGap = 10f
        val thirdW = (CONTENT_WIDTH - cardGap * 2) / 3f

        drawMetricBox(
            canvas, fillPaint, strokePaint, boldPaint, regularPaint,
            RectF(MARGIN, y, MARGIN + thirdW, y + 62f),
            bgColor = Color.WHITE,
            labelColor = mutedText,
            valueColor = slateText,
            label = "NET TAXABLE BASE",
            value = GstCalculatorEngine.formatMoney(result.netTaxableAmount, currency)
        )
        drawMetricBox(
            canvas, fillPaint, strokePaint, boldPaint, regularPaint,
            RectF(MARGIN + thirdW + cardGap, y, MARGIN + 2 * thirdW + cardGap, y + 62f),
            bgColor = Color.rgb(254, 243, 199),
            labelColor = amberAccent,
            valueColor = amberAccent,
            label = "TOTAL TAX (${GstCalculatorEngine.formatRate(result.input.gstRatePercent)}% GST)",
            value = GstCalculatorEngine.formatMoney(result.totalTaxWithCess, currency)
        )
        drawMetricBox(
            canvas, fillPaint, strokePaint, boldPaint, regularPaint,
            RectF(MARGIN + 2 * (thirdW + cardGap), y, PAGE_WIDTH - MARGIN, y + 62f),
            bgColor = emeraldLight,
            labelColor = emeraldDark,
            valueColor = emeraldDark,
            label = "GRAND TOTAL",
            value = GstCalculatorEngine.formatMoney(result.grandTotalAmount, currency)
        )
        y += 76f

        // 4. Tax Breakdown Table
        val rows = buildList {
            add("Entered Unit Price" to GstCalculatorEngine.formatMoney(result.input.amount, currency))
            if (result.input.quantity > 1) {
                add("Quantity" to "${result.input.quantity} units")
            }
            if (result.discountAmount > BigDecimal.ZERO) {
                add(
                    "Discount (${GstCalculatorEngine.formatRate(result.input.discountPercent)}%)" to
                        "- ${GstCalculatorEngine.formatMoney(result.discountAmount, currency)}"
                )
            }
            add("Net Taxable Value" to GstCalculatorEngine.formatMoney(result.netTaxableAmount, currency))
            if (result.input.supplyType == SupplyType.INTRA_STATE) {
                add(
                    "CGST (${GstCalculatorEngine.formatRate(result.cgstRatePercent)}%)" to
                        GstCalculatorEngine.formatMoney(result.cgstAmount, currency)
                )
                add(
                    "SGST (${GstCalculatorEngine.formatRate(result.sgstRatePercent)}%)" to
                        GstCalculatorEngine.formatMoney(result.sgstAmount, currency)
                )
            } else {
                add(
                    "IGST (${GstCalculatorEngine.formatRate(result.igstRatePercent)}%)" to
                        GstCalculatorEngine.formatMoney(result.igstAmount, currency)
                )
            }
            if (result.cessAmount > BigDecimal.ZERO) {
                add(
                    "Compensation Cess (${GstCalculatorEngine.formatRate(result.input.cessRatePercent)}%)" to
                        GstCalculatorEngine.formatMoney(result.cessAmount, currency)
                )
            }
            add("Total GST + Cess (${GstCalculatorEngine.formatRate(result.effectiveTaxSharePercent)}% of Gross)" to GstCalculatorEngine.formatMoney(result.totalTaxWithCess, currency))
            add("Gross Invoice Total" to GstCalculatorEngine.formatMoney(result.grandTotalAmount, currency))
        }

        val tableHeaderH = 24f
        val rowH = 20f
        y = ensureSpace(tableHeaderH + rows.size * rowH + 20f, y)
        boldPaint.textSize = 11f
        boldPaint.color = emeraldDark
        canvas.drawText("TAX BREAKDOWN SUMMARY", MARGIN, y + 10f, boldPaint)
        y += 16f

        fillPaint.color = emeraldDark
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + tableHeaderH), 6f, 6f, fillPaint)
        val thPaint = Paint(boldPaint).apply { color = Color.WHITE; textSize = 9.5f }
        canvas.drawText("Component", MARGIN + 12f, y + 15f, thPaint)
        thPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Amount", PAGE_WIDTH - MARGIN - 12f, y + 15f, thPaint)
        y += tableHeaderH

        rows.forEachIndexed { idx, (label, valStr) ->
            val isLast = idx == rows.lastIndex
            fillPaint.color = when {
                isLast -> emeraldLight
                idx % 2 == 1 -> rowAlt
                else -> Color.WHITE
            }
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowH, fillPaint)
            canvas.drawLine(MARGIN, y + rowH, PAGE_WIDTH - MARGIN, y + rowH, strokePaint)

            val pLeft = if (isLast) boldPaint else regularPaint
            pLeft.textSize = 9.5f
            pLeft.color = if (isLast) emeraldDark else slateText
            pLeft.textAlign = Paint.Align.LEFT
            canvas.drawText(label, MARGIN + 12f, y + 13.5f, pLeft)

            val pRight = if (isLast) boldPaint else regularPaint
            pRight.textSize = 9.5f
            pRight.color = if (isLast) emeraldDark else slateText
            pRight.textAlign = Paint.Align.RIGHT
            canvas.drawText(valStr, PAGE_WIDTH - MARGIN - 12f, y + 13.5f, pRight)
            y += rowH
        }
        boldPaint.textAlign = Paint.Align.LEFT
        regularPaint.textAlign = Paint.Align.LEFT
        y += 16f

        // 5. Step-by-Step Calculation Breakdown
        y = ensureSpace(30f, y)
        boldPaint.textSize = 11f
        boldPaint.color = emeraldDark
        canvas.drawText("STEP-BY-STEP CALCULATION BREAKDOWN", MARGIN, y + 10f, boldPaint)
        y += 18f

        result.steps.forEach { step ->
            val stepH = 50f
            y = ensureSpace(stepH + 8f, y)
            fillPaint.color = rowAlt
            canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + stepH), 7f, 7f, fillPaint)
            canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + stepH), 7f, 7f, strokePaint)

            // Step circle badge
            fillPaint.color = emeraldDark
            canvas.drawCircle(MARGIN + 18f, y + 17f, 9f, fillPaint)
            val numPaint = Paint(boldPaint).apply {
                color = Color.WHITE
                textSize = 9f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("${step.stepNumber}", MARGIN + 18f, y + 20f, numPaint)

            boldPaint.textSize = 10f
            boldPaint.color = slateText
            canvas.drawText(step.title, MARGIN + 34f, y + 19f, boldPaint)

            boldPaint.textSize = 10f
            boldPaint.color = emeraldDark
            boldPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(step.resultFormatted, PAGE_WIDTH - MARGIN - 12f, y + 19f, boldPaint)
            boldPaint.textAlign = Paint.Align.LEFT

            monoPaint.textSize = 8.5f
            canvas.drawText("Formula: ${step.formula}", MARGIN + 34f, y + 32f, monoPaint)

            regularPaint.textSize = 8.5f
            regularPaint.color = mutedText
            canvas.drawText(step.calculation.take(88), MARGIN + 34f, y + 43f, regularPaint)

            y += stepH + 7f
        }

        y += 8f

        // 6. All GST Slabs Quick Comparison Table
        val compRows = result.slabComparison
        val compNeeded = 24f + 20f + compRows.size * 18f + 16f
        y = ensureSpace(compNeeded, y)

        boldPaint.textSize = 11f
        boldPaint.color = emeraldDark
        canvas.drawText("ALL GST RATES COMPARISON (${result.input.taxMode.shortLabel.uppercase()})", MARGIN, y + 10f, boldPaint)
        y += 16f

        fillPaint.color = emeraldDark
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 20f), 5f, 5f, fillPaint)
        val chPaint = Paint(boldPaint).apply { color = Color.WHITE; textSize = 8.5f }
        canvas.drawText("GST Slab", MARGIN + 12f, y + 13.5f, chPaint)
        canvas.drawText("Base Amount", MARGIN + 140f, y + 13.5f, chPaint)
        canvas.drawText("Total GST", MARGIN + 290f, y + 13.5f, chPaint)
        chPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Gross Total", PAGE_WIDTH - MARGIN - 12f, y + 13.5f, chPaint)
        y += 20f

        compRows.forEachIndexed { idx, item ->
            val isSelected = item.rateValue.compareTo(result.input.gstRatePercent) == 0
            fillPaint.color = when {
                isSelected -> emeraldLight
                idx % 2 == 1 -> rowAlt
                else -> Color.WHITE
            }
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 18f, fillPaint)
            canvas.drawLine(MARGIN, y + 18f, PAGE_WIDTH - MARGIN, y + 18f, strokePaint)

            val rowPaint = if (isSelected) boldPaint else regularPaint
            rowPaint.textSize = 8.5f
            rowPaint.color = if (isSelected) emeraldDark else slateText
            rowPaint.textAlign = Paint.Align.LEFT
            canvas.drawText(item.rateDisplay, MARGIN + 12f, y + 12.5f, rowPaint)
            canvas.drawText(GstCalculatorEngine.formatMoney(item.baseAmount, currency), MARGIN + 140f, y + 12.5f, rowPaint)
            canvas.drawText(GstCalculatorEngine.formatMoney(item.gstAmount, currency), MARGIN + 290f, y + 12.5f, rowPaint)
            rowPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(GstCalculatorEngine.formatMoney(item.totalAmount, currency), PAGE_WIDTH - MARGIN - 12f, y + 12.5f, rowPaint)
            y += 18f
        }
        boldPaint.textAlign = Paint.Align.LEFT
        regularPaint.textAlign = Paint.Align.LEFT

        drawFooter(canvas, regularPaint, mutedText, pageNum)
        document.finishPage(page)

        document.writeTo(outputStream)
        document.close()
    }

    private fun drawMetricBox(
        canvas: Canvas,
        fillPaint: Paint,
        strokePaint: Paint,
        boldPaint: Paint,
        regularPaint: Paint,
        rect: RectF,
        bgColor: Int,
        labelColor: Int,
        valueColor: Int,
        label: String,
        value: String
    ) {
        fillPaint.color = bgColor
        canvas.drawRoundRect(rect, 8f, 8f, fillPaint)
        canvas.drawRoundRect(rect, 8f, 8f, strokePaint)

        regularPaint.textSize = 8f
        regularPaint.color = labelColor
        regularPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(label, rect.left + 10f, rect.top + 20f, regularPaint)

        boldPaint.textSize = 14f
        boldPaint.color = valueColor
        boldPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(value.take(18), rect.left + 10f, rect.top + 44f, boldPaint)
    }

    private fun drawFooter(canvas: Canvas, paint: Paint, mutedColor: Int, pageNum: Int) {
        paint.textSize = 8f
        paint.color = mutedColor
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(
            "Generated offline by Elemensys GST Calculator • Verify HSN/SAC rates with official GST notifications.",
            MARGIN,
            PAGE_HEIGHT - 20f,
            paint
        )
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Page $pageNum", PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 20f, paint)
        paint.textAlign = Paint.Align.LEFT
    }
}
