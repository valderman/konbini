package atoms

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.integer
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parseToEnd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Integer {
    private val inputs = listOf(0, 1, -1, Long.MAX_VALUE, Long.MIN_VALUE, 42, -123)

    @Test
    fun `can parse integer`() {
        inputs.forEach { number ->
            integer.parseToEnd("$number").let { result ->
                assertIs<ParserResult.Ok<Long>>(result)
                assertEquals(number, result.result)
            }
        }
    }

    @Test
    fun `can parse integer when followed by garbage`() {
        inputs.forEach { number ->
            integer.parse("${number}xyz").let { result ->
                assertIs<ParserResult.Ok<Long>>(result)
                assertEquals(number, result.result)
                assertEquals("xyz", result.remainingInput)
            }
        }
    }

    @Test
    fun `can not parse garbage as integer`() {
        listOf("  ", "$123", "", "xyz", "x10", "ff", "e3", ".45").forEach { number ->
            assertIs<ParserResult.Error<Long>>(integer.parse(number))
        }
    }
}
