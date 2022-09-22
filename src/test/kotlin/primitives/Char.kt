package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.char
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Char {
    @Test
    fun `can parse the next char`() {
        val p = parser {
            val c = char()
            assertEquals('x', c)
            assertEquals(1, position)
        }
        val result = p.parse("xyz")
        assertIs<ParserResult.Ok<*>>(result)
        assertEquals("yz", result.remainingInput)
    }

    @Test
    fun `can parse the next char if it is the correct one`() {
        val p = parser {
            val c = char('a', 'b', 'x', 'y')
            assertEquals('x', c)
            assertEquals(1, position)
        }
        val result = p.parse("xyz")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun `can not parse the next char if it is the wrong one`() {
        val p = parser {
            char('a', 'b', 'c')
            kotlin.test.fail("Parser didn't fail.")
        }
        val result = p.parse("xyz")
        assertIs<ParserResult.Error<*>>(result)
    }

    @Test
    fun `can not parse the next char if there isn't one`() {
        val p = parser {
            char()
            kotlin.test.fail("Parser didn't fail.")
        }
        val result = p.parse("")
        assertIs<ParserResult.Error<*>>(result)
    }
}