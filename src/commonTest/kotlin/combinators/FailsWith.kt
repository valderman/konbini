package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.char
import cc.ekblad.konbini.failsWith
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FailsWith {
    @Test
    fun failsWith_replaces_parser_error_message() {
        val p = char('x').failsWith("hello")
        val result = p.parse("foo")
        assertIs<ParserResult.Error>(result)
        assertEquals("hello", result.reason)
    }

    @Test
    fun failsWith_does_not_replace_location_information() {
        val p = parser {
            char('x')
            char('y')
        }.failsWith("hello")
        val result = p.parse("xz")
        assertIs<ParserResult.Error>(result)
        assertEquals("hello", result.reason)
        assertEquals(1, result.position)
    }
}
