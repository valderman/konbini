package atoms

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.doubleQuotedString
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parseToEnd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DoubleQuotedString {
    private val examples: List<Pair<String, String>> = listOf(
        "\"\"" to "",
        "\"hello\"" to "hello",
        "\"\\\"\"" to "\"",
        "\"\\\\\"" to "\\",
        "\"hello\\\"world\"" to "hello\"world",
        "\"p\\ro\\nta\\tto\\b\"" to "p\ro\nta\tto\b",
    )

    @Test
    fun can_parse_quoted_string() {
        examples.forEach { (input, expected) ->
            doubleQuotedString.parseToEnd(input).let { result ->
                assertIs<ParserResult.Ok<String>>(result, "Couldn't parse '$input'.")
                assertEquals(expected, result.result)
            }
        }
    }

    @Test
    fun can_parse_quoted_string_when_followed_by_garbage() {
        examples.forEach { (input, expected) ->
            doubleQuotedString.parse("${input}xyz").let { result ->
                assertIs<ParserResult.Ok<String>>(result)
                assertEquals(expected, result.result)
                assertEquals("xyz", result.remainingInput)
            }
        }
    }

    @Test
    fun can_not_parse_garbage_as_quoted_string() {
        listOf("'''", "''", "$123", "", "xyz", "x10", "ff", "e3").forEach { str ->
            assertIs<ParserResult.Error<String>>(doubleQuotedString.parse(str))
        }
    }
}
