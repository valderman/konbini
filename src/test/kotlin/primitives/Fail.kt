package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Fail {
    @Test
    fun `fail causes parser to fail`() {
        val message = "I just don't feel like parsing today."
        val p = parser {
            fail("I just don't feel like parsing today.")
        }
        val result = p.parse("hello")
        assertIs<ParserResult.Error<Unit>>(result)
        assertEquals(message, result.reason)
        assertEquals(0, result.position)
    }

    @Test
    fun `fail includes correct position information`() {
        val message = "That's enough."
        val p = parser {
            string("hel")
            fail(message)
        }
        val result = p.parse("hello")
        assertIs<ParserResult.Error<Unit>>(result)
        assertEquals(message, result.reason)
        assertEquals(3, result.position)
    }
}
