package cc.ekblad.konbini

/**
 * Represents an ongoing parsing computation.
 */
sealed interface ParserState {
    /**
     * Inspect the next character in the input string, without advancing the parser position.
     * Fails if the parser is already at the end of the input.
     */
    val next: Char

    /**
     * Returns the rest of the string being parsed, without advancing the parser position.
     */
    val rest: String

    /**
     * Current position of the parser.
     */
    var position: Int

    /**
     * Matches the end of the input string.
     */
    fun eof(): Unit

    /**
     * Reads the next character in the input. If the parser is already at the end of the input, parsing fails.
     */
    fun char(): Char

    /**
     * Reads the next `expected.length` characters from the input. Fails if they are not exactly [expected].
     */
    fun string(expected: String): String

    /**
     * Attempts to match [pattern] at the current position in the input, and returns the character sequence that
     * matches, if any. Fails if the pattern could not be matched at this position in the inut.
     */
    fun regex(pattern: Regex): String

    /**
     * Unconditionally fail parsing with the given message.
     */
    fun fail(msg: String): Nothing
}

/**
 * Used internally to signal parser failure, initiating backtracking.
 */
@PublishedApi
internal class FailException(var reason: String, var position: Int) : RuntimeException()

internal class StringParserState(private val input: String) : ParserState {
    private val failException: FailException = FailException("", 0)
    override var position: Int = 0

    override val next: Char
        get() {
            if (position >= input.length) {
                fail("Expected character, but got EOF.")
            }
            return input[position]
        }

    override val rest: String
        get() {
            if (position >= input.length) {
                return ""
            }
            return input.substring(position)
        }

    override fun char(): Char {
        if (position >= input.length) {
            fail("Expected character, but got EOF.")
        }
        val c = input[position]
        position += 1
        return c
    }

    override fun regex(pattern: Regex): String {
        val result = pattern.matchAt(input, position)?.value
            ?: fail("Expected pattern '$pattern', but there was no match.")
        position += result.length
        return result
    }

    override fun string(expected: String): String {
        if (position + expected.length > input.length) {
            fail("Expected '$expected', but got EOF.")
        }

        if (!input.regionMatches(position, expected, 0, expected.length)) {
            fail("Expected '$expected', but got '${input.substring(position, position + expected.length)}'.")
        }
        position += expected.length
        return expected
    }

    override fun fail(msg: String): Nothing {
        failException.reason = msg
        failException.position = position
        throw failException
    }

    override fun eof() {
        if (position < input.length) {
            fail("Expected EOF, but got '$next'.")
        }
    }
}
