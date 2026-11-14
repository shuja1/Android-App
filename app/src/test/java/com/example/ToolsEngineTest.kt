package com.example

import com.example.domain.BasicCalculatorEngine
import com.example.domain.ConverterCategory
import com.example.domain.UnitConverterEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class ToolsEngineTest {

    @Test
    fun basicCalculator_precedenceAndParentheses() {
        val result = BasicCalculatorEngine.evaluate("(1200 + 300) × 2 − 500 ÷ 2")
        assertNotNull(result)
        assertEquals(0, BigDecimal("2750").compareTo(result!!))
    }

    @Test
    fun basicCalculator_contextualPercentage() {
        val added = BasicCalculatorEngine.evaluate("1000 + 18%")
        assertNotNull(added)
        assertEquals(0, BigDecimal("1180").compareTo(added!!))

        val subtracted = BasicCalculatorEngine.evaluate("1000 − 10%")
        assertNotNull(subtracted)
        assertEquals(0, BigDecimal("900").compareTo(subtracted!!))
    }

    @Test
    fun basicCalculator_divideByZeroReturnsNull() {
        assertNull(BasicCalculatorEngine.evaluate("100 ÷ 0"))
    }

    @Test
    fun unitConverter_lengthAndWeight() {
        val meters = UnitConverterEngine.getUnits(ConverterCategory.LENGTH).first { it.id == "m" }
        val cm = UnitConverterEngine.getUnits(ConverterCategory.LENGTH).first { it.id == "cm" }
        assertEquals("250", UnitConverterEngine.convertSingle(ConverterCategory.LENGTH, "2.5", meters, cm))

        val kg = UnitConverterEngine.getUnits(ConverterCategory.WEIGHT).first { it.id == "kg" }
        val g = UnitConverterEngine.getUnits(ConverterCategory.WEIGHT).first { it.id == "g" }
        assertEquals("1,500", UnitConverterEngine.convertSingle(ConverterCategory.WEIGHT, "1.5", kg, g))
    }

    @Test
    fun unitConverter_temperatureAndNumberBase() {
        val celsius = UnitConverterEngine.getUnits(ConverterCategory.TEMPERATURE).first { it.id == "c" }
        val fahrenheit = UnitConverterEngine.getUnits(ConverterCategory.TEMPERATURE).first { it.id == "f" }
        assertEquals("212", UnitConverterEngine.convertSingle(ConverterCategory.TEMPERATURE, "100", celsius, fahrenheit))

        val dec = UnitConverterEngine.getUnits(ConverterCategory.NUMBER_BASE).first { it.id == "dec" }
        val hex = UnitConverterEngine.getUnits(ConverterCategory.NUMBER_BASE).first { it.id == "hex" }
        val bin = UnitConverterEngine.getUnits(ConverterCategory.NUMBER_BASE).first { it.id == "bin" }
        assertEquals("FF", UnitConverterEngine.convertSingle(ConverterCategory.NUMBER_BASE, "255", dec, hex))
        assertEquals("11111111", UnitConverterEngine.convertSingle(ConverterCategory.NUMBER_BASE, "255", dec, bin))
    }
}
