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
    fun can_parse_integer() {
        inputs.forEach { number ->
            integer.parseToEnd("$number").let { result ->
                assertIs<ParserResult.Ok<Long>>(result)
                assertEquals(number, result.result)
            }
        }
    }

    @Test
    fun can_parse_integer_when_followed_by_garbage() {
        inputs.forEach { number ->
            integer.parse("${number}xyz").let { result ->
                assertIs<ParserResult.Ok<Long>>(result)
                assertEquals(number, result.result)
                assertEquals("xyz", result.remainingInput)
            }
        }
    }

    @Test
    fun can_not_parse_garbage_as_integer() {
        listOf("  ", "$123", "", "xyz", "x10", "ff", "e3", ".45").forEach { number ->
            assertIs<ParserResult.Error>(integer.parse(number))
        }
    }

    @Test
    fun can_not_parse_garbage_too_large_or_too_small_number_as_integer() {
        assertIs<ParserResult.Error>(integer.parse("1000000000000000000000000000"))
        assertIs<ParserResult.Error>(integer.parse("-1000000000000000000000000000"))
    }
}
