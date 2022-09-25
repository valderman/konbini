package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EOF {
    @Test
    fun can_parse_eof_at_beginning_of_empty_string() {
        val p = parser {
            eof()
            assertEquals(0, position)
        }
        val result = p.parse("")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun can_parse_eof_at_the_end_of_input() {
        val p = parser {
            string("hej")
            eof()
            assertEquals(3, position)
        }
        val result = p.parse("hej")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun can_not_parse_eof_unless_at_the_end_of_input() {
        val p = parser {
            string("he")
            eof()
            kotlin.test.fail("Parser did not fail.")
        }
        val result = p.parse("hej")
        assertIs<ParserResult.Error<*>>(result)
    }
}
