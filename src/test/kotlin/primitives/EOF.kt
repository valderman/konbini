package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EOF {
    @Test
    fun `can parse eof at beginning of empty string`() {
        val p = parser {
            eof()
            assertEquals(0, position)
        }
        val result = p.parse("")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun `can parse eof at the end of input`() {
        val p = parser {
            string("hej")
            eof()
            assertEquals(3, position)
        }
        val result = p.parse("hej")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun `can not parse eof unless at the end of input`() {
        val p = parser {
            string("he")
            eof()
            kotlin.test.fail("Parser did not fail.")
        }
        val result = p.parse("hej")
        assertIs<ParserResult.Error<*>>(result)
    }
}