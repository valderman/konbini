package parser

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.parseToEnd
import cc.ekblad.konbini.string
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ParseToEnd {
    @Test
    fun `parseToEnd fails if there is leftover input`() {
        val result = string("hello\nhel").parseToEnd("hello\nhello")
        assertIs<ParserResult.Error<String>>(result)
        assertEquals(9, result.position)
        assertEquals(2, result.line)
        assertEquals(4, result.column)
    }

    @Test
    fun `parseToEnd fails if there is leftover whitespace`() {
        val result = string("hello").parseToEnd("hello\n")
        assertIs<ParserResult.Error<String>>(result)
        assertEquals(5, result.position)
        assertEquals(1, result.line)
        assertEquals(6, result.column)
    }

    @Test
    fun `parseToEnd fails if there is leading whitespace`() {
        val result = string("hello").parseToEnd(" hello")
        assertIs<ParserResult.Error<String>>(result)
        assertEquals(0, result.position)
        assertEquals(1, result.line)
        assertEquals(1, result.column)
    }

    @Test
    fun `parseToEnd ignores leading and trailing whitespace if ignoreWhitespace = true`() {
        val result = string("hello").parseToEnd(" hello ", ignoreWhitespace = true)
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hello", result.result)
        assertEquals("", result.remainingInput)
    }
}
