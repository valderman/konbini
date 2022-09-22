package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Rest {
    @Test
    fun `rest returns all remaining input without advancing position`() {
        val p = parser {
            assertEquals("hello", rest)
            assertEquals(0, position)
            assertEquals('h', char())

            assertEquals("ello", rest)
            assertEquals(1, position)
        }
        val result = p.parse("hello")
        assertIs<ParserResult.Ok<Unit>>(result)
    }

    @Test
    fun `rest does not fail on eof`() {
        val p = parser {
            string("hello")
            assertEquals("", rest)
        }
        val result = p.parse("hello")
        assertIs<ParserResult.Ok<Unit>>(result)
    }
}
