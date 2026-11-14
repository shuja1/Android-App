package com.example.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class ConverterCategory(val title: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    AREA("Area"),
    VOLUME("Volume"),
    TEMPERATURE("Temperature"),
    SPEED("Speed"),
    TIME("Time"),
    DATA("Data"),
    PRESSURE("Pressure"),
    ENERGY("Energy"),
    NUMBER_BASE("Number Base")
}

data class ConverterUnit(
    val id: String,
    val name: String,
    val symbol: String,
    val factorToBase: BigDecimal = BigDecimal.ONE,
    val radix: Int = 10
)

data class UnitConversionRow(
    val unit: ConverterUnit,
    val formattedValue: String
)

object UnitConverterEngine {

    private val MC = MathContext(16, RoundingMode.HALF_UP)

    val unitsByCategory: Map<ConverterCategory, List<ConverterUnit>> = mapOf(
        ConverterCategory.LENGTH to listOf(
            ConverterUnit("mm", "Millimeter", "mm", BigDecimal("0.001")),
            ConverterUnit("cm", "Centimeter", "cm", BigDecimal("0.01")),
            ConverterUnit("m", "Meter", "m", BigDecimal("1")),
            ConverterUnit("km", "Kilometer", "km", BigDecimal("1000")),
            ConverterUnit("in", "Inch", "in", BigDecimal("0.0254")),
            ConverterUnit("ft", "Foot", "ft", BigDecimal("0.3048")),
            ConverterUnit("yd", "Yard", "yd", BigDecimal("0.9144")),
            ConverterUnit("mi", "Mile", "mi", BigDecimal("1609.344"))
        ),
        ConverterCategory.WEIGHT to listOf(
            ConverterUnit("mg", "Milligram", "mg", BigDecimal("0.000001")),
            ConverterUnit("g", "Gram", "g", BigDecimal("0.001")),
            ConverterUnit("kg", "Kilogram", "kg", BigDecimal("1")),
            ConverterUnit("q", "Quintal", "q", BigDecimal("100")),
            ConverterUnit("t", "Metric Ton", "t", BigDecimal("1000")),
            ConverterUnit("oz", "Ounce", "oz", BigDecimal("0.028349523125")),
            ConverterUnit("lb", "Pound", "lb", BigDecimal("0.45359237"))
        ),
        ConverterCategory.AREA to listOf(
            ConverterUnit("sq_mm", "Square Millimeter", "mm²", BigDecimal("0.000001")),
            ConverterUnit("sq_cm", "Square Centimeter", "cm²", BigDecimal("0.0001")),
            ConverterUnit("sq_m", "Square Meter", "m²", BigDecimal("1")),
            ConverterUnit("sq_km", "Square Kilometer", "km²", BigDecimal("1000000")),
            ConverterUnit("sq_in", "Square Inch", "in²", BigDecimal("0.00064516")),
            ConverterUnit("sq_ft", "Square Foot", "ft²", BigDecimal("0.09290304")),
            ConverterUnit("sq_yd", "Square Yard", "yd²", BigDecimal("0.83612736")),
            ConverterUnit("acre", "Acre", "ac", BigDecimal("4046.8564224")),
            ConverterUnit("ha", "Hectare", "ha", BigDecimal("10000"))
        ),
        ConverterCategory.VOLUME to listOf(
            ConverterUnit("ml", "Milliliter", "mL", BigDecimal("0.001")),
            ConverterUnit("l", "Liter", "L", BigDecimal("1")),
            ConverterUnit("kl", "Kiloliter", "kL", BigDecimal("1000")),
            ConverterUnit("m3", "Cubic Meter", "m³", BigDecimal("1000")),
            ConverterUnit("fl_oz", "Fluid Ounce (US)", "fl oz", BigDecimal("0.0295735295625")),
            ConverterUnit("cup", "Cup (US)", "cup", BigDecimal("0.2365882365")),
            ConverterUnit("pt", "Pint (US)", "pt", BigDecimal("0.473176473")),
            ConverterUnit("gal", "Gallon (US)", "gal", BigDecimal("3.785411784"))
        ),
        ConverterCategory.TEMPERATURE to listOf(
            ConverterUnit("c", "Celsius", "°C"),
            ConverterUnit("f", "Fahrenheit", "°F"),
            ConverterUnit("k", "Kelvin", "K")
        ),
        ConverterCategory.SPEED to listOf(
            ConverterUnit("mps", "Meters / second", "m/s", BigDecimal("1")),
            ConverterUnit("kmph", "Kilometers / hour", "km/h", BigDecimal("0.2777777777777778")),
            ConverterUnit("mph", "Miles / hour", "mph", BigDecimal("0.44704")),
            ConverterUnit("knot", "Knot", "kn", BigDecimal("0.5144444444444445"))
        ),
        ConverterCategory.TIME to listOf(
            ConverterUnit("ms", "Millisecond", "ms", BigDecimal("0.001")),
            ConverterUnit("s", "Second", "s", BigDecimal("1")),
            ConverterUnit("min", "Minute", "min", BigDecimal("60")),
            ConverterUnit("hr", "Hour", "hr", BigDecimal("3600")),
            ConverterUnit("day", "Day", "d", BigDecimal("86400")),
            ConverterUnit("wk", "Week", "wk", BigDecimal("604800")),
            ConverterUnit("yr", "Year (365d)", "yr", BigDecimal("31536000"))
        ),
        ConverterCategory.DATA to listOf(
            ConverterUnit("b", "Byte", "B", BigDecimal("1")),
            ConverterUnit("kb", "Kilobyte (1024 B)", "KB", BigDecimal("1024")),
            ConverterUnit("mb", "Megabyte", "MB", BigDecimal("1048576")),
            ConverterUnit("gb", "Gigabyte", "GB", BigDecimal("1073741824")),
            ConverterUnit("tb", "Terabyte", "TB", BigDecimal("1099511627776")),
            ConverterUnit("pb", "Petabyte", "PB", BigDecimal("1125899906842624"))
        ),
        ConverterCategory.PRESSURE to listOf(
            ConverterUnit("pa", "Pascal", "Pa", BigDecimal("1")),
            ConverterUnit("kpa", "Kilopascal", "kPa", BigDecimal("1000")),
            ConverterUnit("bar", "Bar", "bar", BigDecimal("100000")),
            ConverterUnit("psi", "Pounds / sq in", "psi", BigDecimal("6894.757293168361")),
            ConverterUnit("atm", "Atmosphere", "atm", BigDecimal("101325"))
        ),
        ConverterCategory.ENERGY to listOf(
            ConverterUnit("j", "Joule", "J", BigDecimal("1")),
            ConverterUnit("kj", "Kilojoule", "kJ", BigDecimal("1000")),
            ConverterUnit("cal", "Calorie", "cal", BigDecimal("4.184")),
            ConverterUnit("kcal", "Kilocalorie", "kcal", BigDecimal("4184")),
            ConverterUnit("wh", "Watt-hour", "Wh", BigDecimal("3600")),
            ConverterUnit("kwh", "Kilowatt-hour", "kWh", BigDecimal("3600000"))
        ),
        ConverterCategory.NUMBER_BASE to listOf(
            ConverterUnit("dec", "Decimal", "DEC", radix = 10),
            ConverterUnit("bin", "Binary", "BIN", radix = 2),
            ConverterUnit("oct", "Octal", "OCT", radix = 8),
            ConverterUnit("hex", "Hexadecimal", "HEX", radix = 16)
        )
    )

    fun getUnits(category: ConverterCategory): List<ConverterUnit> =
        unitsByCategory[category].orEmpty()

    fun convertSingle(
        category: ConverterCategory,
        rawInput: String,
        fromUnit: ConverterUnit,
        toUnit: ConverterUnit
    ): String {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return "0"

        if (category == ConverterCategory.NUMBER_BASE) {
            return convertBase(trimmed, fromUnit.radix, toUnit.radix)
        }

        val inputVal = trimmed.toBigDecimalOrNull() ?: return "—"
        val converted = when (category) {
            ConverterCategory.TEMPERATURE -> convertTemperature(inputVal, fromUnit.id, toUnit.id)
            else -> {
                val baseVal = inputVal.multiply(fromUnit.factorToBase, MC)
                baseVal.divide(toUnit.factorToBase, MC)
            }
        }
        return formatNumericResult(converted)
    }

    fun convertAll(
        category: ConverterCategory,
        rawInput: String,
        fromUnit: ConverterUnit
    ): List<UnitConversionRow> {
        val units = getUnits(category)
        return units.map { target ->
            UnitConversionRow(
                unit = target,
                formattedValue = convertSingle(category, rawInput, fromUnit, target)
            )
        }
    }

    private fun convertTemperature(value: BigDecimal, fromId: String, toId: String): BigDecimal {
        if (fromId == toId) return value
        val nine = BigDecimal("9")
        val five = BigDecimal("5")
        val thirtyTwo = BigDecimal("32")
        val kelvinOffset = BigDecimal("273.15")

        val celsius = when (fromId) {
            "c" -> value
            "f" -> value.subtract(thirtyTwo, MC).multiply(five, MC).divide(nine, MC)
            "k" -> value.subtract(kelvinOffset, MC)
            else -> value
        }

        return when (toId) {
            "c" -> celsius
            "f" -> celsius.multiply(nine, MC).divide(five, MC).add(thirtyTwo, MC)
            "k" -> celsius.add(kelvinOffset, MC)
            else -> celsius
        }
    }

    private fun convertBase(raw: String, fromRadix: Int, toRadix: Int): String {
        val clean = raw.trim().uppercase()
        if (clean == "-") return "-"
        val parsed = clean.toBigIntegerOrNull(fromRadix) ?: return "Invalid"
        return parsed.toString(toRadix).uppercase()
    }

    private fun formatNumericResult(value: BigDecimal): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) return "0"
        val absVal = value.abs()
        return if (absVal >= BigDecimal("1000000000000") || absVal < BigDecimal("0.000001")) {
            val sci = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            sci.format(value)
        } else {
            val normalized = value.setScale(8, RoundingMode.HALF_UP).stripTrailingZeros()
            val plain = if (normalized.scale() < 0) {
                normalized.setScale(0, RoundingMode.UNNECESSARY).toPlainString()
            } else {
                normalized.toPlainString()
            }
            val parts = plain.split(".")
            val intPart = parts[0].toBigDecimalOrNull() ?: return plain
            val intFmt = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.US)).format(intPart)
            if (parts.size > 1) "$intFmt.${parts[1]}" else intFmt
        }
    }
}
