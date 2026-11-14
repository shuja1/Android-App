package com.example.data

import com.example.domain.GstCalculatorEngine
import com.example.domain.HsnProductItem
import java.math.BigDecimal

object HsnDirectoryData {

    val items: List<HsnProductItem> = listOf(
        // 0% Exempt
        HsnProductItem(
            hsnCode = "0401",
            productName = "Fresh Milk & Pasteurized Milk",
            category = "Dairy & Essentials",
            gstRate = BigDecimal("0"),
            updateNote = "Exempt from GST across loose and packaged fresh milk."
        ),
        HsnProductItem(
            hsnCode = "0701–0709",
            productName = "Fresh Vegetables & Fruits",
            category = "Agriculture & Food",
            gstRate = BigDecimal("0"),
            updateNote = "Nil-rated fresh agricultural produce."
        ),
        HsnProductItem(
            hsnCode = "4901",
            productName = "Printed Books & Educational Manuals",
            category = "Books & Stationery",
            gstRate = BigDecimal("0"),
            updateNote = "Exempt to support education and publishing."
        ),
        HsnProductItem(
            hsnCode = "2501",
            productName = "Common Edible Salt",
            category = "Food Essentials",
            gstRate = BigDecimal("0"),
            updateNote = "Nil-rated essential household item."
        ),

        // 0.25%
        HsnProductItem(
            hsnCode = "7102",
            productName = "Rough & Uncut Diamonds",
            category = "Gems & Stones",
            gstRate = BigDecimal("0.25"),
            updateNote = "Special 0.25% slab for rough industrial and uncut diamonds."
        ),
        HsnProductItem(
            hsnCode = "7103",
            productName = "Rough Precious & Semi-Precious Stones",
            category = "Gems & Stones",
            gstRate = BigDecimal("0.25"),
            updateNote = "0.125% CGST + 0.125% SGST for unworked stones."
        ),

        // 1%
        HsnProductItem(
            hsnCode = "9954",
            productName = "Affordable Residential Housing (Without ITC)",
            category = "Real Estate & Construction",
            gstRate = BigDecimal("1"),
            updateNote = "1% effective rate for eligible affordable housing units.",
            isServiceSac = true
        ),
        HsnProductItem(
            hsnCode = "COMPOSITION",
            productName = "Composition Scheme Goods Supply",
            category = "Small Business Levy",
            gstRate = BigDecimal("1"),
            updateNote = "1% turnover tax (0.5% CGST + 0.5% SGST) for eligible traders/manufacturers."
        ),

        // 3%
        HsnProductItem(
            hsnCode = "7108",
            productName = "Gold Bullion, Bars & Coins",
            category = "Precious Metals",
            gstRate = BigDecimal("3"),
            updateNote = "Standard 3% bullion slab (1.5% CGST + 1.5% SGST)."
        ),
        HsnProductItem(
            hsnCode = "7113",
            productName = "Gold, Silver & Platinum Jewellery",
            category = "Jewellery",
            gstRate = BigDecimal("3"),
            updateNote = "3% on jewellery value; separate making charges (9988) if billed separately."
        ),
        HsnProductItem(
            hsnCode = "7106",
            productName = "Silver Unwrought & Semi-Manufactured",
            category = "Precious Metals",
            gstRate = BigDecimal("3"),
            updateNote = "3% GST on silver ingots, bars, and powder."
        ),

        // 5%
        HsnProductItem(
            hsnCode = "1006",
            productName = "Pre-Packaged & Labelled Rice",
            category = "Food & Grains",
            gstRate = BigDecimal("5"),
            updateNote = "Updated rule: 5% applies to pre-packaged & labelled packs (<=25 kg)."
        ),
        HsnProductItem(
            hsnCode = "1904",
            productName = "Fortified Rice Kernels (FRK)",
            category = "Food & Nutrition",
            gstRate = BigDecimal("5"),
            updateNote = "Recent GST Council update: Reduced uniformly to 5%."
        ),
        HsnProductItem(
            hsnCode = "0901 / 0902",
            productName = "Tea Leaves, Roasted Coffee & Spices",
            category = "Food & Beverages",
            gstRate = BigDecimal("5"),
            updateNote = "5% GST on packaged tea, coffee beans, turmeric, cumin, and pepper."
        ),
        HsnProductItem(
            hsnCode = "3004",
            productName = "Life-Saving & Essential Medicines",
            category = "Healthcare & Pharma",
            gstRate = BigDecimal("5"),
            updateNote = "5% concessional rate on notified oncology & essential formulations."
        ),
        HsnProductItem(
            hsnCode = "8803 / 8807",
            productName = "Aircraft Parts & MRO Components",
            category = "Aviation",
            gstRate = BigDecimal("5"),
            updateNote = "Recent GST Council update: Uniform 5% IGST on aircraft parts/tools."
        ),
        HsnProductItem(
            hsnCode = "6109 / 6205",
            productName = "Apparel & Garments (Up to ₹1,000)",
            category = "Textiles & Apparel",
            gstRate = BigDecimal("5"),
            updateNote = "5% on readymade garments priced at or below ₹1,000 per piece."
        ),
        HsnProductItem(
            hsnCode = "996331",
            productName = "Non-AC / Standard Restaurant Dining",
            category = "Hospitality",
            gstRate = BigDecimal("5"),
            updateNote = "5% without Input Tax Credit (2.5% CGST + 2.5% SGST).",
            isServiceSac = true
        ),

        // 12%
        HsnProductItem(
            hsnCode = "4819",
            productName = "Corrugated & Non-Corrugated Carton Boxes",
            category = "Packaging",
            gstRate = BigDecimal("12"),
            updateNote = "Recent GST Council update: Reduced from 18% to uniform 12%."
        ),
        HsnProductItem(
            hsnCode = "7310",
            productName = "Steel, Iron & Aluminium Milk Cans",
            category = "Dairy Equipment",
            gstRate = BigDecimal("12"),
            updateNote = "Recent GST Council update: Standardized at 12% regardless of use."
        ),
        HsnProductItem(
            hsnCode = "7321",
            productName = "Solar Cookers",
            category = "Clean Energy & Appliances",
            gstRate = BigDecimal("12"),
            updateNote = "Recent GST Council update: Unified at 12% GST."
        ),
        HsnProductItem(
            hsnCode = "0405",
            productName = "Butter, Ghee & Dairy Spreads",
            category = "Dairy & Food",
            gstRate = BigDecimal("12"),
            updateNote = "12% GST on packaged butter, ghee, and cheese (0406)."
        ),
        HsnProductItem(
            hsnCode = "8424",
            productName = "Fire Water Sprinklers & Agricultural Sprayers",
            category = "Industrial & Agri",
            gstRate = BigDecimal("12"),
            updateNote = "Recent GST Council clarification: All sprinklers covered at 12%."
        ),
        HsnProductItem(
            hsnCode = "6205 / 6403",
            productName = "Apparel (> ₹1,000) & Footwear",
            category = "Apparel & Footwear",
            gstRate = BigDecimal("12"),
            updateNote = "12% on garments above ₹1,000 and standard footwear."
        ),
        HsnProductItem(
            hsnCode = "996425",
            productName = "Business Class Air Travel",
            category = "Transport & Travel",
            gstRate = BigDecimal("12"),
            updateNote = "12% with ITC on business-class passenger air transport.",
            isServiceSac = true
        ),

        // 18%
        HsnProductItem(
            hsnCode = "8517",
            productName = "Smartphones, Routers & Telecom Equipment",
            category = "Electronics & IT",
            gstRate = BigDecimal("18"),
            updateNote = "Standard 18% electronics slab (9% CGST + 9% SGST)."
        ),
        HsnProductItem(
            hsnCode = "8471",
            productName = "Laptops, Desktops & Computer Hardware",
            category = "Electronics & IT",
            gstRate = BigDecimal("18"),
            updateNote = "18% GST on computing machines, SSDs, and peripherals."
        ),
        HsnProductItem(
            hsnCode = "8528",
            productName = "LED Monitors, Projectors & Smart TVs",
            category = "Consumer Electronics",
            gstRate = BigDecimal("18"),
            updateNote = "Monitors up to 32 inches & standard TVs taxed at 18%."
        ),
        HsnProductItem(
            hsnCode = "998314",
            productName = "IT, Software, SaaS & Cloud Consulting",
            category = "IT Services (SAC)",
            gstRate = BigDecimal("18"),
            updateNote = "18% standard rate for software design, SaaS & digital services.",
            isServiceSac = true
        ),
        HsnProductItem(
            hsnCode = "998311",
            productName = "Management, Legal & Accounting Services",
            category = "Professional Services (SAC)",
            gstRate = BigDecimal("18"),
            updateNote = "18% GST on business consulting, audit, and advisory.",
            isServiceSac = true
        ),
        HsnProductItem(
            hsnCode = "9403",
            productName = "Office & Home Furniture",
            category = "Furniture & Interiors",
            gstRate = BigDecimal("18"),
            updateNote = "18% GST on wooden, metal, and modular furniture."
        ),
        HsnProductItem(
            hsnCode = "3923",
            productName = "Plastic Packaging Containers & Bottles",
            category = "Industrial Packaging",
            gstRate = BigDecimal("18"),
            updateNote = "18% GST on commercial plastic crates, jars, and closures."
        ),

        // 28%
        HsnProductItem(
            hsnCode = "8415",
            productName = "Air Conditioners & Split AC Units",
            category = "Heavy Appliances",
            gstRate = BigDecimal("28"),
            updateNote = "28% GST slab on domestic & commercial air conditioning units."
        ),
        HsnProductItem(
            hsnCode = "2523",
            productName = "Portland Cement & Building Cement",
            category = "Construction Materials",
            gstRate = BigDecimal("28"),
            updateNote = "28% GST (14% CGST + 14% SGST) on cement."
        ),
        HsnProductItem(
            hsnCode = "8703",
            productName = "Passenger Motor Vehicles",
            category = "Automobiles",
            gstRate = BigDecimal("28"),
            updateNote = "28% base GST (+ Compensation Cess 1%–22% by engine/length)."
        ),
        HsnProductItem(
            hsnCode = "8711",
            productName = "Motorcycles (Standard & > 350cc)",
            category = "Automobiles",
            gstRate = BigDecimal("28"),
            updateNote = "28% GST; > 350cc attracts additional 3% cess."
        ),

        // 40%
        HsnProductItem(
            hsnCode = "2202",
            productName = "Aerated & Caffeinated Beverages (28% + 12% Cess)",
            category = "Beverages (Effective 40%)",
            gstRate = BigDecimal("40"),
            updateNote = "Combined effective tax burden (28% GST + 12% Compensation Cess)."
        ),
        HsnProductItem(
            hsnCode = "2402 / 2403",
            productName = "Luxury / Sin Demerit & Statutory Ceiling Slab",
            category = "Demerit Goods",
            gstRate = BigDecimal("40"),
            updateNote = "40% statutory ceiling / combined high-rate luxury & sin calculation."
        )
    )

    val categories: List<String> by lazy {
        items.map { it.category }.distinct().sorted()
    }

    fun filter(
        query: String,
        rateFilter: String = "ALL",
        categoryFilter: String = "ALL"
    ): List<HsnProductItem> {
        val q = query.trim().lowercase()
        val isExplicitPercentQuery = q.endsWith("%") && q.length > 1
        val normalizedRateQuery = q.removeSuffix("%").trim()
        return items.filter { item ->
            val rateStr = GstCalculatorEngine.formatRate(item.gstRate)
            val rateDisplay = "$rateStr%"
            val matchesRate = rateFilter == "ALL" || rateStr == rateFilter
            val matchesCategory = categoryFilter == "ALL" || item.category == categoryFilter
            val matchesQuery = when {
                q.isEmpty() -> true
                isExplicitPercentQuery -> rateDisplay == q || rateStr == normalizedRateQuery
                else -> item.hsnCode.lowercase().contains(q) ||
                    item.productName.lowercase().contains(q) ||
                    item.category.lowercase().contains(q) ||
                    item.updateNote.lowercase().contains(q) ||
                    rateDisplay == "$q%" ||
                    rateStr == q
            }
            matchesRate && matchesCategory && matchesQuery
        }
    }
}
