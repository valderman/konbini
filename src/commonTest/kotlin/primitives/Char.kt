package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.char
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.Char
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Char {
    @Test
    fun can_parse_the_next_char() {
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
    fun can_parse_the_next_char_if_it_is_the_correct_one() {
        val p = parser {
            val c = char('a', 'b', 'x', 'y')
            assertEquals('x', c)
            assertEquals(1, position)
        }
        val result = p.parse("xyz")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun can_not_parse_the_next_char_if_it_is_the_wrong_one() {
        val result = char('a', 'b', 'c').parse("xyz")
        assertIs<ParserResult.Error>(result)
    }

    @Test
    fun can_not_parse_the_next_char_if_there_isnt_one() {
        assertIs<ParserResult.Error>(char.parse(""))
        assertIs<ParserResult.Error>(parser { char() }.parse(""))
    }

    @Test
    fun char_without_arguments_accepts_any_char() {
        val result = char().parse("xyz")
        assertIs<ParserResult.Ok<Char>>(result)
        assertEquals('x', result.result)
    }
}
