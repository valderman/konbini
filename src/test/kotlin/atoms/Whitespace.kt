package atoms

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.whitespace
import cc.ekblad.konbini.whitespace1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Whitespace {
    @Test
    fun `can parse whitespace`() {
        fun assertResult(result: ParserResult<String>, input: String, remainingInput: String) {
            assertIs<ParserResult.Ok<String>>(result)
            assertEquals(input.removeSuffix(remainingInput), result.result)
            assertEquals(remainingInput, result.remainingInput)
        }

        listOf(
            "  \r\n\t fgsfds " to "fgsfds ",
            "  \r\n\t  " to "",
        ).forEach { (input, remainingInput) ->
            assertResult(whitespace.parse(input), input, remainingInput)
            assertResult(whitespace1.parse(input), input, remainingInput)
        }
    }

    @Test
    fun `optional whitespace parser succeeds on non-whitespace`() {
        whitespace.parse("hello").let {
            assertIs<ParserResult.Ok<String>>(it)
            assertEquals("", it.result)
            assertEquals("hello", it.remainingInput)
        }
    }

    @Test
    fun `mandatory whitespace parser fails on non-whitespace`() {
        whitespace1.parse("hello").let {
            assertIs<ParserResult.Error<String>>(it)
        }
    }
}
