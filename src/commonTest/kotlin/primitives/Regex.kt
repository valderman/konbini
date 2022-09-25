package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.regex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Regex {
    @Test
    fun can_regex_if_it_matches() {
        val p = parser {
            val c = regex(Regex("f[o]*o"))
            assertEquals("foooooo", c)
            assertEquals(7, position)
        }
        val result = p.parse("foooooobar")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun can_not_parse_regex_if_it_doesnt_match() {
        val p = parser {
            regex(Regex("b[oy]ar"))
            kotlin.test.fail("Parser didn't fail.")
        }
        val result = p.parse("bar")
        assertIs<ParserResult.Error>(result)
    }

    @Test
    fun can_not_parse_a_nonempty_regex_at_the_end_of_the_input() {
        val result = regex(Regex(".")).parse("")
        assertIs<ParserResult.Error>(result)
    }

    @Test
    fun can_parse_a_regex_matching_an_empty_string_even_at_eof() {
        val p = parser {
            val c = regex(Regex("yes|(nope)?"))
            assertEquals("", c)
            assertEquals(0, position)
        }
        val result = p.parse("")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun can_not_parse_regex_if_eof_happens_before_a_match() {
        val result = regex("hello").parse("hell")
        assertIs<ParserResult.Error>(result)
    }
}
