package parser

import cc.ekblad.konbini.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Parse {
    @Test
    fun `parse returns the result and remaining input of a parser`() {
        val result = string("hel").parse("hello")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hel", result.result)
        assertEquals("lo", result.remainingInput)
    }

    @Test
    fun `parse does not ignore whitespace by default`() {
        val result = string("hel").parse(" hello")
        assertIs<ParserResult.Error<String>>(result)
        assertEquals(0, result.position)
    }

    @Test
    fun `parse accurately reports 1-based line and column of error`() {
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
        assertIs<ParserResult.Error<String>>(result)
        assertEquals(11, result.position)
        assertEquals(2, result.line)
        assertEquals(6, result.column)
    }

    @Test
    fun `parse accurately reports 1-based line and column of error on first line`() {
        val p = parser {
            string("hel")
            string("la")
        }
        val result = p.parse("hello\ntiny world\nfrom konbini")
        assertIs<ParserResult.Error<String>>(result)
        assertEquals(3, result.position)
        assertEquals(1, result.line)
        assertEquals(4, result.column)
    }

    @Test
    fun `parse does not eat whitespace at the end of a string even when skipWhitespace = true`() {
        val result = string("hel").parse("hello ", skipWhitespace = true)
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hel", result.result)
        assertEquals("lo ", result.remainingInput)
    }
}