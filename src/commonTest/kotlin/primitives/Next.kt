package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Next {
    @Test
    fun next_returns_the_next_character_of_the_input_with_advancing_position() {
        val p = parser {
            assertEquals('h', next)
            assertEquals(0, position)
            assertEquals('h', char())

            assertEquals('e', next)
            assertEquals(1, position)
        }
        val result = p.parse("hello")
        assertIs<ParserResult.Ok<Unit>>(result)
    }

    @Test
    fun next_fails_on_eof() {
        val p = parser {
            string("hello")
            next
        }
        val result = p.parse("hello")
        assertIs<ParserResult.Error<Char>>(result)
        assertEquals(5, result.position)
    }
}
