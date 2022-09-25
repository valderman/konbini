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
    fun tryParse_does_not_fail() {
        val p = tryParse {
            fail("no parse for you")
        }
        val result = p.parse<String?>("hello")
        assertIs<ParserResult.Ok<String?>>(result)
        assertEquals("hello", result.remainingInput)
        assertNull(result.result)
    }

    @Test
    fun tryParse_returns_null_when_subparser_fails() {
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
    fun tryParse_does_not_mess_up_position_when_subparser_fails() {
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
