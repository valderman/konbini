package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.char
import cc.ekblad.konbini.fail
import cc.ekblad.konbini.oneOf
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.string
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OneOf {
    @Test
    fun `oneOf fails if all alternatives fail`() {
        val result = oneOf(string("foo"), string("bar"), string("baz")).parse("hello")
        assertIs<ParserResult.Error<String>>(result)
    }

    @Test
    fun `parsers are matched left to right`() {
        val result = oneOf(string("foo"), string("foot")).parse("foot")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("foo", result.result)
    }

    @Test
    fun `if the first parser fails, the second one is tried`() {
        val result = oneOf(fail("nope"), string("foot")).parse("foot")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("foot", result.result)
    }

    @Test
    fun `oneOf properly backtracks with compound parsers`() {
        val result = oneOf(
            parser { char('f') ; char('x') },
            string("foot")
        ).parse("foot")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("foot", result.result)
    }
}