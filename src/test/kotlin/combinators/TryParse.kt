package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.map
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.tryParse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class TryParse {
    @Test
    fun `tryParse does not fail`() {
        val p = tryParse {
            fail("no parse for you")
        }
        val result = p.parse<String?>("hello")
        assertIs<ParserResult.Ok<String?>>(result)
        assertEquals("hello", result.remainingInput)
        assertNull(result.result)
    }

    @Test
    fun `tryParse returns null when sub-parser fails`() {
        val p = tryParse<String> {
            string("hel")
            fail("none shall pass")
        }.map { assertNull(it) ; it }
        val result = p.parse("hello")
        assertIs<ParserResult.Ok<String?>>(result)
        assertEquals("hello", result.remainingInput)
        assertNull(result.result)
    }

    @Test
    fun `tryParse does not mess up position when sub-parser fails`() {
        val p = parser {
            tryParse<String> {
                string("hel")
                fail("none shall pass")
            }
            string("hello")
        }
        val result = p.parse("hello")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("", result.remainingInput)
        assertEquals("hello", result.result)
    }
}
