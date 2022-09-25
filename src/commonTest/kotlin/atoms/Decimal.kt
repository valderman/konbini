package atoms

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.decimal
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parseToEnd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Decimal {
    private val examples: List<Pair<String, Double>> = listOf(
        "0" to 0.0,
        "0.1" to 0.1,
        "-0.1" to -0.1,
        "1" to 1.0,
        "-1" to -1.0,
        "2.5" to 2.5,
        "-50.05" to -50.05,
        "1e3" to 1000.0,
        "+12" to 12.0,
        "-2E2" to -200.0,
        "-2E-2" to -0.02,
    )

    @Test
    fun cand_parse_decimal() {
        examples.forEach { (input, expected) ->
            decimal.parseToEnd(input).let { result ->
                assertIs<ParserResult.Ok<Double>>(result, "Couldn't parse '$input'.")
                assertEquals(expected, result.result)
            }
        }
    }

    @Test
    fun can_parse_decimal_when_followed_by_garbage() {
        examples.forEach { (input, expected) ->
            decimal.parse("${input}xyz").let { result ->
                assertIs<ParserResult.Ok<Double>>(result)
                assertEquals(expected, result.result)
                assertEquals("xyz", result.remainingInput)
            }
        }
    }

    @Test
    fun can_not_parse_garbage_as_decimal() {
        listOf("  ", "$123", "", "xyz", "x10", "ff", "e3").forEach { number ->
            assertIs<ParserResult.Error>(decimal.parse(number))
        }
    }
}
