package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.char
import cc.ekblad.konbini.fail
import cc.ekblad.konbini.oneOf
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.string
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OneOf {
    @Test
    fun oneOf_fails_if_all_alternatives_fail() {
        oneOf(string("foo"), string("bar"), string("baz")).parse("hello").let {
            assertIs<ParserResult.Error>(it)
        }
        parser { oneOf(parser { string("nope") }) }.parse("foot").let {
            assertIs<ParserResult.Error>(it)
        }
    }

    @Test
    fun parsers_are_matched_left_to_right() {
        val result = oneOf(string("foo"), string("foot")).parse("foot")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("foo", result.result)
    }

    @Test
    fun if_the_first_parser_fails_the_second_one_is_tried() {
        val result = oneOf(fail("nope"), string("foot")).parse("foot")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("foot", result.result)
    }

    @Test
    fun oneOf_properly_backtracks_with_compound_parsers() {
        val result = oneOf(
            parser { char('f') ; char('x') },
            string("foot")
        ).parse("foot")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("foot", result.result)
    }

    @Test
    fun oneOf_error_message_includes_labels() {
        val result = oneOf(
            "fx" to parser { char('f') ; char('x') },
            "foot" to { string("fo") ; string("ot") }
        ).parse("foo")
        assertIs<ParserResult.Error>(result)
        assertContains(result.reason, "fx")
        assertContains(result.reason, "foot")

        // Line info indicates entire parser
        assertEquals(1, result.column)
        assertEquals(1, result.line)
        assertEquals(0, result.position)
    }

    @Test
    fun oneOf_error_message_includes_last_subparser_error_if_no_labels_are_given() {
        val result = oneOf(
            fail("not this error"),
            { string("fo") ; fail("THIS error!") }
        ).parse("foo")
        assertIs<ParserResult.Error>(result)
        assertEquals("THIS error!", result.reason)
        assertEquals(3, result.column)
        assertEquals(1, result.line)
        assertEquals(2, result.position)
    }
}
