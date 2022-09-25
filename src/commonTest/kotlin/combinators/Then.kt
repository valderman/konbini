package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.char
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.then
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Then {
    @Test
    fun then_does_not_fail_atomically() {
        val p = char('x').then(char('y'))
        val result = p.parse("xz")
        assertIs<ParserResult.Error>(result)
        assertEquals(1, result.position)
    }

    @Test
    fun then_does_not_mess_up_position_when_it_succeeds() {
        val p = char('x').then(char('y'))
        val result = p.parse("xyz")
        assertIs<ParserResult.Ok<Pair<Char, Char>>>(result)
        assertEquals(Pair('x', 'y'), result.result)
        assertEquals("z", result.remainingInput)
    }
}
