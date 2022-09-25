package integration

import cc.ekblad.konbini.Parser
import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.boolean
import cc.ekblad.konbini.bracket
import cc.ekblad.konbini.chain
import cc.ekblad.konbini.char
import cc.ekblad.konbini.decimal
import cc.ekblad.konbini.doubleQuotedString
import cc.ekblad.konbini.map
import cc.ekblad.konbini.oneOf
import cc.ekblad.konbini.parseToEnd
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.string
import cc.ekblad.konbini.whitespace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class JSON {
    private val comma = parser { whitespace() ; char(',') ; whitespace() }
    private val colon = parser { whitespace() ; char(':') ; whitespace() }
    private val pKeyValue = parser { doubleQuotedString().also { colon() } to pValue() }
    private val pAtom = oneOf(decimal, doubleQuotedString, boolean, string("null").map { null })
    private val pArray = bracket(
        parser { char('[') ; whitespace() },
        parser { whitespace() ; char(']') },
        parser { chain(pValue, comma).terms },
    )
    private val pDict = bracket(
        parser { char('{') ; whitespace() },
        parser { whitespace() ; char('}') },
        parser { chain(pKeyValue, comma).terms.toMap() },
    )
    private val pValue: Parser<Any?> = oneOf(pAtom, pDict, pArray)

    @Test
    fun `can parse atomic json values`() {
        val atoms = listOf(
            "true" to true,
            "false" to false,
            "null" to null,
            "123" to 123.0,
            "3.1" to 3.1,
            "-115" to -115.0,
            "2e2" to 200.0,
            "\"i'm a string\"" to "i'm a string",
            "\"escape\nchars\"" to "escape\nchars"
        )
        atoms.forEach { (input, expected) ->
            val result = pValue.parseToEnd(input)
            assertIs<ParserResult.Ok<Any?>>(result, "Couldn't parse '$input'.")
            assertEquals(expected, result.result)
        }
    }

    @Test
    fun `can parse lists`() {
        val examples = listOf(
            "[]" to emptyList<Double>(),
            "[1]" to listOf(1.0),
            "[ 2 ]" to listOf(2.0),
            "[1,2,3]" to listOf(1.0, 2.0, 3.0),
            "[1\n,2,\n3\n]" to listOf(1.0, 2.0, 3.0),
            "[1, [2 , 3 ]]" to listOf(1.0, listOf(2.0, 3.0)),
        )
        examples.forEach { (input, expected) ->
            val result = pValue.parseToEnd(input)
            assertIs<ParserResult.Ok<Any?>>(result, "Couldn't parse '$input'.")
            assertEquals(expected, result.result)
        }
    }

    @Test
    fun `can parse dicts`() {
        val examples = listOf(
            "{}" to emptyMap<String, Any?>(),
            "{  }" to emptyMap<String, Any?>(),
            """{"x":"y"}""" to mapOf("x" to "y"),
            """{ "x" : "y" }""" to mapOf("x" to "y"),
            "{\n\"x\"\n:\n\"y\"\n}" to mapOf("x" to "y"),
            """{"foo":null,"bar":123}""" to mapOf("foo" to null, "bar" to 123.0),
            """{"foo":null,"bar":{ "x": 123, "y" :{"z":[[null]] ,"t":"hello\nworld"} }}""" to mapOf(
                "foo" to null,
                "bar" to mapOf(
                    "x" to 123.0,
                    "y" to mapOf(
                        "z" to listOf(listOf(null)),
                        "t" to "hello\nworld"
                    )
                )
            ),
        )
        examples.forEach { (input, expected) ->
            val result = pValue.parseToEnd(input)
            assertIs<ParserResult.Ok<Any?>>(result, "Couldn't parse '$input'.")
            assertEquals(expected, result.result)
        }
    }

    @Test
    fun `can parse complex json value`() {
        val expected = mapOf(
            "foo" to listOf(listOf("bar"), "baz", 42.0),
            "bar" to null,
            "baz" to mapOf(
                "123" to mapOf("null" to 456.0),
                "456" to true
            )
        )
        val input = """
            {
                "foo": [["bar"],"baz" , 42],
                "bar":null,
                "baz" :{
                    "123" : {"null":456},
                    "456": true
                }
            }
        """.trimIndent()
        val result = pValue.parseToEnd(input)
        assertIs<ParserResult.Ok<Map<String, Any?>>>(result)
        assertEquals(expected, result.result)
    }
}