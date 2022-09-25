package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.atomically
import cc.ekblad.konbini.char
import cc.ekblad.konbini.parse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Atomically {
    @Test
    fun `atomic parser fails atomically`() {
        val p = atomically {
            char('x')
            char('y')
        }
        val result = p.parse("xz")
        assertIs<ParserResult.Error<Char>>(result)
        assertEquals(0, result.position)
    }

    @Test
    fun `atomic parser does not mess up position when it succeeds`() {
        val p = atomically {
            char('x')
            char('y')
        }
        val result = p.parse("xyz")
        assertIs<ParserResult.Ok<Char>>(result)
        assertEquals('y', result.result)
        assertEquals("z", result.remainingInput)
    }
}
