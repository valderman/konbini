package parser

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.string
import cc.ekblad.konbini.whitespace1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Parse {
    @Test
    fun parse_returns_the_result_and_remaining_input_of_a_parser() {
        val result = string("hel").parse("hello")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hel", result.result)
        assertEquals("lo", result.remainingInput)
    }

    @Test
    fun parse_does_not_ignore_whitespace_by_default() {
        val result = string("hel").parse(" hello")
        assertIs<ParserResult.Error>(result)
        assertEquals(0, result.position)
    }

    @Test
    fun parse_accurately_reports_1_based_line_and_column_of_error() {
        val p = parser {
            string("hello")
            whitespace1()
            string("tiny")
            whitespace1()
            string("planet")
            whitespace1()
            string("from konbini")
        }
        val result = p.parse("hello\ntiny world\nfrom konbini")
        assertIs<ParserResult.Error>(result)
        assertEquals(11, result.position)
        assertEquals(2, result.line)
        assertEquals(6, result.column)
    }

    @Test
    fun parse_accurately_reports_1_based_line_and_column_of_error_on_first_line() {
        val p = parser {
            string("hel")
            string("la")
        }
        val result = p.parse("hello\ntiny world\nfrom konbini")
        assertIs<ParserResult.Error>(result)
        assertEquals(3, result.position)
        assertEquals(1, result.line)
        assertEquals(4, result.column)
    }

    @Test
    fun parse_does_not_eat_whitespace_at_the_end_of_a_string_even_when_skipWhitespace_is_true() {
        val result = string("hel").parse("hello ", skipWhitespace = true)
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hel", result.result)
        assertEquals("lo ", result.remainingInput)
    }
}
