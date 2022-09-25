package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class String {
    @Test
    fun can_parse_the_next_string_if_it_is_the_correct_one() {
        val p = parser {
            val c = string("foo")
            assertEquals("foo", c)
            assertEquals(3, position)
        }
        val result = p.parse("foobar")
        assertIs<ParserResult.Ok<*>>(result)
    }

    @Test
    fun can_not_parse_the_next_string_if_it_is_the_wrong_one() {
        val p = parser {
            string("foo")
            kotlin.test.fail("Parser didn't fail.")
        }
        val result = p.parse("bar")
        assertIs<ParserResult.Error<*>>(result)
    }

    @Test
    fun can_not_parse_the_next_string_if_there_isnt_one() {
        val p = parser {
            string("hello")
            kotlin.test.fail("Parser didn't fail.")
        }
        val result = p.parse("")
        assertIs<ParserResult.Error<*>>(result)
    }

    @Test
    fun can_not_parse_the_next_string_if_its_too_long() {
        val p = parser {
            string("hello")
            kotlin.test.fail("Parser didn't fail.")
        }
        val result = p.parse("hell")
        assertIs<ParserResult.Error<*>>(result)
    }
}
