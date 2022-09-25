package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.char
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Parser {
    @Test
    fun `parser does not fail atomically`() {
        val p = parser {
            char('x')
            char('y')
        }
        val result = p.parse("xz")
        assertIs<ParserResult.Error<Char>>(result)
        assertEquals(1, result.position)
    }

    @Test
    fun `parser does not mess up position when it succeeds`() {
        val p = parser {
            char('x')
            char('y')
        }
        val result = p.parse("xyz")
        assertIs<ParserResult.Ok<Char>>(result)
        assertEquals('y', result.result)
        assertEquals("z", result.remainingInput)
    }
}
