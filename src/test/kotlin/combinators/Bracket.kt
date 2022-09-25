package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.bracket
import cc.ekblad.konbini.char
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.regex
import cc.ekblad.konbini.string
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Bracket {
    @Test
    fun `bracket fails atomically when p fails`() {
        val p = bracket(char('('), char(')'), string("hello"))
        val result = p.parse("(hullo)")
        assertIs<ParserResult.Error<String>>(result)
        assertEquals(0, result.position)
    }

    @Test
    fun `bracket fails atomically when closing bracket fails`() {
        val p = bracket(char('('), char(')'), string("hello"))
        val result = p.parse("(hello]")
        assertIs<ParserResult.Error<String>>(result)
        assertEquals(0, result.position)
    }

    @Test
    fun `can parse bracketed expression`() {
        val p = bracket(char('('), char(')'), regex("\\w+"))
        val result = p.parse("(hello)(world)")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("hello", result.result)
        assertEquals("(world)", result.remainingInput)
    }
}
