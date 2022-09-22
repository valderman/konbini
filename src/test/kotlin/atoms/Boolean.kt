package atoms

import cc.ekblad.konbini.*
import kotlin.Boolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Boolean {
    private val examples: List<Pair<String, Boolean>> = listOf(
        "true" to true,
        "false" to false,
    )

    @Test
    fun `can parse boolean`() {
        examples.forEach { (input, expected) ->
            boolean.parseToEnd(input).let { result ->
                assertIs<ParserResult.Ok<Boolean>>(result, "Couldn't parse '$input'.")
                assertEquals(expected, result.result)
            }
        }
    }

    @Test
    fun `can parse boolean when followed by non-word character`() {
        examples.forEach { (input, expected) ->
            boolean.parse("${input}!").let { result ->
                assertIs<ParserResult.Ok<Boolean>>(result)
                assertEquals(expected, result.result)
                assertEquals("!", result.remainingInput)
            }
        }
    }

    @Test
    fun `can not parse garbage as boolean`() {
        listOf("tru", "f", "hello", "truefgs", "asdfalse", "  ", "$123", "", "0", "x10", "ff", "e3").forEach { number ->
            assertIs<ParserResult.Error<Boolean>>(boolean.parse(number))
        }
    }
}
