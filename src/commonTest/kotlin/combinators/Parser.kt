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
    fun parser_does_not_fail_atomically() {
        val p = parser {
            char('x')
            char('y')
        }
        val result = p.parse("xz")
        assertIs<ParserResult.Error>(result)
        assertEquals(1, result.position)
    }

    @Test
    fun parser_does_not_mess_up_position_when_it_succeeds() {
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
