@file:Suppress("NOTHING_TO_INLINE")
package cc.ekblad.konbini

private val whitespaceRegex = Regex("\\s*")

/**
 * Parser matching zero or more whitespace characters.
 */
val whitespace: Parser<String> = regex(whitespaceRegex)

private val whitespace1Regex = Regex("\\s+")

/**
 * Parser matching one or more whitespace characters.
 */
val whitespace1: Parser<String> = regex(whitespace1Regex)

private val digitsRegex = Regex("[+\\-]?[0-9]+")

/**
 * Parser that matches a 64-bit signed integer in base 10.
 *
 * If the parser matches a string of digits but the number represented by the digits is outside the range representable
 * by a 64-bit signed integer, the parser fails.
 */
val integer: Parser<Long> = parser {
    val digits = regex(digitsRegex)
    digits.toLongOrNull()
        ?: fail("Expected a 64-bit integer, but '$digits' is outside its representable range.")
}

private val decimalRegex = Regex("[+\\-]?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+\\-]?\\d+)?")

/**
 * Parser that matches a floating point number. Supports both decimal and scientific notation.
 */
val decimal: Parser<Double> = parser {
    regex(decimalRegex).toDouble()
}

private val booleanRegex = Regex("(true|false)\\b")

/**
 * Caser-sensitive parser that matches either `true` and `false`.
 */
val boolean: Parser<Boolean> = parser {
    regex(booleanRegex).toBooleanStrict()
}

private inline fun quotedString(regex: Regex): Parser<String> = regex(regex).map {
    it.substring(1, it.lastIndex)
        .replace("\\'", "'")
        .replace("\\\"", "\"")
        .replace("\\\\", "\\")
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\b", "\b")
}

private val doubleQuotedStringRegex = Regex(""""[^\\"]*(\\["nrtb\\][^\\"]*)*"""")

/**
 * Parser matching a double-quoted string, possibly containing escape codes.
 * Supported escape codes are \\, \", \n, \r, \t, and \b.
 */
val doubleQuotedString: Parser<String> = quotedString(doubleQuotedStringRegex)

private val singleQuotedStringRegex = Regex("""'[^\\']*(\\['nrtb\\][^\\']*)*'""")

/**
 * Parser matching a single-quoted string, possibly containing escape codes.
 * Supported escape codes are \\, \', \n, \r, \t, and \b.
 */
val singleQuotedString: Parser<String> = quotedString(singleQuotedStringRegex)

/**
 * Consumes the next character and fails if it is not in the given list of expected characters.
 * If the list of expected characters is empty, the parser will match any character.
 */
inline fun ParserState.char(vararg expected: Char): Char {
    if (next !in expected && expected.isNotEmpty()) {
        fail("Expected one of ${expected.joinToString()} but got '$next'.")
    }
    return char()
}

/**
 * Creates a [ParserState.char] parser.
 */
inline fun char(vararg expected: Char) = parser { char(*expected) }

/**
 * Creates a parser which matches exactly one character.
 */
var char: Parser<Char> = char()

/**
 * Matches the given regular expression [pattern], returning the text that matched it.
 * Fails if [pattern] could not be matched at the current point in the parser input.
 */
inline fun ParserState.regex(pattern: String): String =
    regex(Regex(pattern))

/**
 * Creates [ParserState.regex] parser.
 */
inline fun regex(pattern: String): Parser<String> {
    val re = Regex(pattern)
    return regex(re)
}

/**
 * Creates [ParserState.regex] parser.
 */
inline fun regex(pattern: Regex): Parser<String> = parser { regex(pattern) }

/**
 * Creates a [ParserState.string] parser.
 */
inline fun string(expected: String) = parser { string(expected) }

/**
 * Creates a [ParserState.fail] parser.
 */
inline fun fail(reason: String) = parser { fail(reason) }
