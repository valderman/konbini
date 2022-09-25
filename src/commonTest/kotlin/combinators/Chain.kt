package combinators

import cc.ekblad.konbini.Chain
import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.chain
import cc.ekblad.konbini.chain1
import cc.ekblad.konbini.chainl
import cc.ekblad.konbini.chainr
import cc.ekblad.konbini.char
import cc.ekblad.konbini.integer
import cc.ekblad.konbini.map
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.string
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Chain {
    private sealed class Expr {
        data class Lit(val value: Long) : Expr()
        data class Add(val lhs: Expr, val rhs: Expr) : Expr()
    }

    @Test
    fun chain_succeeds_on_empty_chain() {
        val result = chain(char('c'), char(',')).parse("hello")
        assertIs<ParserResult.Ok<Chain<Char, Char>>>(result)
        assertEquals(Chain.empty(), result.result)
        assertEquals("hello", result.remainingInput)
    }

    @Test
    fun chain1_fails_on_empty_chain() {
        val result = chain1(char('c'), char(',')).parse("hello")
        assertIs<ParserResult.Error>(result)
        assertEquals(0, result.position)
    }

    @Test
    fun chain1_parses_one_or_more_elements() {
        val result = parser {
            chain1(
                parser { char('c') },
                parser { char(',') }
            )
        }.parse("c,c,d")
        assertIs<ParserResult.Ok<Chain<Char, Char>>>(result)
        assertEquals(listOf('c', 'c'), result.result.terms)
        assertEquals(listOf(','), result.result.separators)
        assertEquals(",d", result.remainingInput)
    }

    @Test
    fun chain_parses_comma_separated_lists_properly() {
        val result = chain(char, char(',')).parse("a,b,c,d,e")
        assertIs<ParserResult.Ok<Chain<Char, Char>>>(result)
        assertEquals(listOf('a', 'b', 'c', 'd', 'e'), result.result.terms)
        assertEquals(listOf(',', ',', ',', ','), result.result.separators)
    }

    @Test
    fun chain_parses_singleton_lists_properly() {
        val result = chain(char('x'), char(',')).parse("xyz")
        assertIs<ParserResult.Ok<Chain<Char, Char>>>(result)
        assertEquals(listOf('x'), result.result.terms)
        assertEquals("yz", result.remainingInput)
    }

    @Test
    fun chainl_fails_on_empty_chain() {
        val result = chainl(string("x"), char(',')) { x, _, _ -> x }.parse("abc")
        assertIs<ParserResult.Error>(result)
    }

    @Test
    fun chainr_fails_on_empty_chain() {
        val result = chainr(string("x"), char(',')) { x, _, _ -> x }.parse("abc")
        assertIs<ParserResult.Error>(result)
    }

    @Test
    fun chainl_is_left_associative() {
        val result = chainl<Expr, Char>(
            integer.map(Expr::Lit),
            char('+')
        ) { x, y, _ -> Expr.Add(x, y) }.parse("1+2+3")
        assertIs<ParserResult.Ok<Expr>>(result)
        assertEquals(Expr.Add(Expr.Add(Expr.Lit(1), Expr.Lit(2)), Expr.Lit(3)), result.result)
    }

    @Test
    fun chainr_is_right_associative() {
        val result = chainr<Expr, Char>(
            integer.map(Expr::Lit),
            char('+')
        ) { x, y, _ -> Expr.Add(x, y) }.parse("1+2+3")
        assertIs<ParserResult.Ok<Expr>>(result)
        assertEquals(Expr.Add(Expr.Lit(1), Expr.Add(Expr.Lit(2), Expr.Lit(3))), result.result)
    }
}
