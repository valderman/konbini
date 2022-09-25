package parser

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parseToEnd
import cc.ekblad.konbini.string
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ParseToEnd {
    @Test
    fun parseToEnd_fails_if_there_is_leftover_input() {
        val result = string("hello\nhel").parseToEnd("hello\nhello")
        assertIs<ParserResult.Error>(result)
        assertEquals(9, result.position)
        assertEquals(2, result.line)
        assertEquals(4, result.column)
    }

    @Test
    fun parseToEnd_fails_if_there_is_leftover_whitespace() {
        val result = string("hello").parseToEnd("hello\n")
        assertIs<ParserResult.Error>(result)
        assertEquals(5, result.position)
        assertEquals(1, result.line)
        assertEquals(6, result.column)
    }

    @Test
    fun parseToEnd_fails_if_there_is_leading_whitespace() {
        val result = string("hello").parseToEnd(" hello")
        assertIs<ParserResult.Error>(result)
        assertEquals(0, result.position)
        assertEquals(1, result.line)
        assertEquals(1, result.column)
    }

    @Test
    fun parseToEnd_ignores_leading_and_trailing_whitespace_if_ignoreWhitespace_is_true() {
        val result = string("hello").parseToEnd(" hello ", ignoreWhitespace = true)
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hello", result.result)
        assertEquals("", result.remainingInput)
    }
}
