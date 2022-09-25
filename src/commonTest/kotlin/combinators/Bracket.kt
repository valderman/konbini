package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.bracket
import cc.ekblad.konbini.char
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.regex
import cc.ekblad.konbini.string
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Bracket {
    @Test
    fun bracket_fails_atomically_when_p_fails() {
        val p = bracket(char('('), char(')'), string("hello"))
        val result = p.parse("(hullo)")
        assertIs<ParserResult.Error>(result)
        assertEquals(0, result.position)
    }

    @Test
    fun bracket_fails_atomically_when_closing_bracket_fails() {
        val p = bracket(char('('), char(')'), string("hello"))
        val result = p.parse("(hello]")
        assertIs<ParserResult.Error>(result)
        assertEquals(0, result.position)
    }

    @Test
    fun can_parse_bracketed_expression() {
        val p = bracket(char('('), char(')'), regex("\\w+"))
        val result = p.parse("(hello)(world)")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hello", result.result)
        assertEquals("(world)", result.remainingInput)
    }

    @Test
    fun non_inlined_bracket_can_parse_bracketed_expression() {
        val p = parser {
            bracket(
                parser { char('(') },
                parser { char(')') },
                parser { regex("\\w+") }
            )
        }
        val result = p.parse("(hello)(world)")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hello", result.result)
        assertEquals("(world)", result.remainingInput)
    }
}
