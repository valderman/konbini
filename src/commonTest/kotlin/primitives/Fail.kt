package primitives

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.fail
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Fail {
    @Test
    fun fail_causes_parser_to_fail() {
        val message = "I just don't feel like parsing today."
        val result = fail(message).parse("hello")
        assertIs<ParserResult.Error<Unit>>(result)
        assertEquals(message, result.reason)
        assertEquals(0, result.position)
    }

    @Test
    fun fail_includes_correct_position_information() {
        val message = "That's enough."
        val p = parser {
            string("hel")
            fail(message)
        }
        val result = p.parse("hello")
        assertIs<ParserResult.Error<Unit>>(result)
        assertEquals(message, result.reason)
        assertEquals(3, result.position)
    }
}
