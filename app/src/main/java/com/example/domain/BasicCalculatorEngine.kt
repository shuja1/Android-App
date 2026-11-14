package com.example.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class BasicCalcHistoryItem(
    val id: String,
    val expression: String,
    val resultFormatted: String,
    val rawResult: BigDecimal
)

object BasicCalculatorEngine {

    private val MC = MathContext(16, RoundingMode.HALF_UP)
    private val HUNDRED = BigDecimal("100")

    fun evaluate(expression: String): BigDecimal? {
        val cleaned = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(",", "")
            .trim()
        if (cleaned.isEmpty()) return null

        return runCatching {
            val tokens = tokenize(cleaned)
            if (tokens.isEmpty()) return null
            val parser = Parser(tokens)
            val value = parser.parseExpression()
            if (parser.hasMore()) return null
            value
        }.getOrNull()
    }

    fun formatResult(value: BigDecimal): String {
        val normalized = value.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        val plain = if (normalized.scale() < 0) {
            normalized.setScale(0, RoundingMode.UNNECESSARY).toPlainString()
        } else {
            normalized.toPlainString()
        }
        val parts = plain.split(".")
        val intPart = parts[0]
        val fracPart = parts.getOrNull(1)
        val intVal = intPart.toBigDecimalOrNull() ?: return plain
        val symbols = DecimalFormatSymbols(Locale.US)
        val intFormatted = DecimalFormat("#,##0", symbols).format(intVal)
        return if (fracPart != null) "$intFormatted.$fracPart" else intFormatted
    }

    fun toPlainInputString(value: BigDecimal): String {
        val normalized = value.setScale(8, RoundingMode.HALF_UP).stripTrailingZeros()
        return if (normalized.scale() <= 0) {
            normalized.setScale(0, RoundingMode.HALF_UP).toPlainString()
        } else {
            normalized.toPlainString()
        }
    }

    private sealed interface Token {
        data class Number(val value: BigDecimal) : Token
        data class Op(val symbol: Char) : Token
        data object LParen : Token
        data object RParen : Token
        data object Percent : Token
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        while (i < input.length) {
            val ch = input[i]
            when {
                ch.isWhitespace() -> i++
                ch.isDigit() || ch == '.' -> {
                    val start = i
                    var dotCount = 0
                    while (i < input.length && (input[i].isDigit() || input[i] == '.')) {
                        if (input[i] == '.') dotCount++
                        if (dotCount > 1) error("Invalid number")
                        i++
                    }
                    val numStr = input.substring(start, i)
                    if (numStr == ".") error("Lone dot")
                    tokens.add(Token.Number(BigDecimal(numStr)))
                }
                ch == '(' -> {
                    // Implicit multiplication like 2(3) or (1)(2)
                    if (tokens.lastOrNull() is Token.Number ||
                        tokens.lastOrNull() is Token.RParen ||
                        tokens.lastOrNull() is Token.Percent
                    ) {
                        tokens.add(Token.Op('*'))
                    }
                    tokens.add(Token.LParen)
                    i++
                }
                ch == ')' -> {
                    tokens.add(Token.RParen)
                    i++
                }
                ch == '%' -> {
                    tokens.add(Token.Percent)
                    i++
                }
                ch == '+' || ch == '-' || ch == '*' || ch == '/' -> {
                    tokens.add(Token.Op(ch))
                    i++
                }
                else -> error("Unexpected char: $ch")
            }
        }
        return tokens
    }

    private class Parser(private val tokens: List<Token>) {
        private var pos = 0

        fun hasMore(): Boolean = pos < tokens.size

        private fun peek(): Token? = tokens.getOrNull(pos)
        private fun consume(): Token = tokens[pos++]

        fun parseExpression(): BigDecimal {
            var left = parseTerm()
            while (true) {
                val next = peek()
                if (next is Token.Op && (next.symbol == '+' || next.symbol == '-')) {
                    consume()
                    val rightIsPercent = isNextTermSinglePercent()
                    val right = parseTerm()
                    left = if (rightIsPercent) {
                        // Contextual percentage: A + B% = A + (A * B/100)
                        val delta = left.multiply(right, MC)
                        if (next.symbol == '+') left.add(delta, MC) else left.subtract(delta, MC)
                    } else {
                        if (next.symbol == '+') left.add(right, MC) else left.subtract(right, MC)
                    }
                } else {
                    break
                }
            }
            return left
        }

        private fun isNextTermSinglePercent(): Boolean {
            return pos + 1 < tokens.size &&
                tokens[pos] is Token.Number &&
                tokens[pos + 1] is Token.Percent &&
                (pos + 2 >= tokens.size || tokens[pos + 2] !is Token.Op ||
                    ((tokens[pos + 2] as Token.Op).symbol != '*' && (tokens[pos + 2] as Token.Op).symbol != '/'))
        }

        private fun parseTerm(): BigDecimal {
            var left = parseUnary()
            while (true) {
                val next = peek()
                if (next is Token.Op && (next.symbol == '*' || next.symbol == '/')) {
                    consume()
                    val right = parseUnary()
                    left = if (next.symbol == '*') {
                        left.multiply(right, MC)
                    } else {
                        if (right.compareTo(BigDecimal.ZERO) == 0) error("Division by zero")
                        left.divide(right, MC)
                    }
                } else {
                    break
                }
            }
            return left
        }

        private fun parseUnary(): BigDecimal {
            val next = peek()
            if (next is Token.Op && (next.symbol == '+' || next.symbol == '-')) {
                consume()
                val value = parseUnary()
                return if (next.symbol == '-') value.negate() else value
            }
            return parsePostfix()
        }

        private fun parsePostfix(): BigDecimal {
            var value = parsePrimary()
            while (peek() is Token.Percent) {
                consume()
                value = value.divide(HUNDRED, MC)
            }
            return value
        }

        private fun parsePrimary(): BigDecimal {
            return when (val token = peek()) {
                is Token.Number -> {
                    consume()
                    token.value
                }
                is Token.LParen -> {
                    consume()
                    val inner = parseExpression()
                    if (peek() !is Token.RParen) error("Missing ')'")
                    consume()
                    inner
                }
                else -> error("Unexpected token")
            }
        }
    }
}
