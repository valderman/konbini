package combinators

import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.char
import cc.ekblad.konbini.fail
import cc.ekblad.konbini.integer
import cc.ekblad.konbini.many
import cc.ekblad.konbini.many1
import cc.ekblad.konbini.oneOf
import cc.ekblad.konbini.parse
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.string
import cc.ekblad.konbini.whitespace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class Many {
    private val examples = listOf(
        Triple("", char, ""),
        Triple("", fail("nope"), ""),
        Triple("ppppp", char('p'), "ppppp"),
        Triple("pppqq", char('p'), "ppp"),
        Triple("qp", char('p'), ""),
        Triple("pqqpqpqppqpq", oneOf(char('p'), char('q')), "pqqpqpqppqpq"),
    )

    @Test
    fun many_can_parse_zero_or_more_instances_of_its_given_parser() {
        examples.forEach { (input, parser, expected) ->
            val result = many(parser).parse(input)
            assertIs<ParserResult.Ok<List<Char>>>(result, "Couldn't parse '$input'.")
            assertEquals(expected, result.result.joinToString(""))
            assertEquals(input.removePrefix(expected), result.remainingInput)
        }
    }

    @Test
    fun many1_can_parse_one_or_more_instances_of_its_given_parser() {
        examples.filter { it.third.isNotEmpty() }.forEach { (input, parser, expected) ->
            listOf(many1(parser), parser { many1(parser) }).forEach { p ->
                val result = p.parse(input)
                assertIs<ParserResult.Ok<List<Char>>>(result, "Couldn't parse '$input'.")
                assertEquals(expected, result.result.joinToString(""))
                assertEquals(input.removePrefix(expected), result.remainingInput)
            }
        }
    }

    @Test
    fun many1_fails_if_it_cant_parse_at_least_one_element() {
        assertIs<ParserResult.Error>(many1(char('x')).parse("y"))
        assertIs<ParserResult.Error>(parser { many1(parser { char('x') }) }.parse("y"))
    }

    @Test
    fun many_parses_the_correct_number_of_elements() {
        val examples = listOf(
            Triple(
                "1 2 3 4 56",
                parser { integer().also { whitespace() } },
                listOf(1, 2, 3, 4, 56L)
            ),
            Triple(
                "aabbabab",
                oneOf(string("ab"), string("a"), string("b")),
                listOf("a", "ab", "b", "ab", "ab")
            ),
        )
        examples.forEach { (input, parser, expected) ->
            val result = many(parser).parse(input)
            assertIs<ParserResult.Ok<List<Char>>>(result, "Couldn't parse '$input'.")
            assertEquals(expected, result.result)
        }
    }

    @Test
    fun many_properly_backtracks_with_compound_parsers() {
        val p = parser {
            many(parser { char('x') ; char('x') })
            string("xyz")
        }
        val result = p.parse("xyz")
        assertIs<ParserResult.Ok<String>>(result)
        assertEquals("xyz", result.result)
    }
}
