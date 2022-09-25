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
    fun `can regex if it matches`() {
        val p = parser {
            val c = regex(Regex("f[o]*o"))
            assertEquals("foooooo", c)
            assertEquals(7, position)
        }
        val result = p.parse("foooooobar")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun `can not parse regex if it doesn't match`() {
        val p = parser {
            regex(Regex("b[oy]ar"))
            kotlin.test.fail("Parser didn't fail.")
        }
        val result = p.parse("bar")
        assertIs<ParserResult.Error<*>>(result)
    }

    @Test
    fun `can not parse a non-empty regex at the end of the input`() {
        val result = regex(Regex(".")).parse("")
        assertIs<ParserResult.Error<*>>(result)
    }

    @Test
    fun `can parse a regex matching an empty string even at eof`() {
        val p = parser {
            val c = regex(Regex("yes|(nope)?"))
            assertEquals("", c)
            assertEquals(0, position)
        }
        val result = p.parse("")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun `can not parse regex if eof happens before a match`() {
        val result = regex("hello").parse("hell")
        assertIs<ParserResult.Error<*>>(result)
    }
}