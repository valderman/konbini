package cc.ekblad.konbini

/**
 * Represents an ongoing parsing computation.
 *
 * Inherit this class if you need to keep your own parsing state as well.
 */
open class ParserState {
    /**
     * The entire input string the parser is working with.
     */
    lateinit var input: String
        internal set

    /**
     * Inspect the next character in the input string, without advancing the parser position.
     * Fails if the parser is already at the end of the input.
     */
    val next: Char
        get() {
            if (position >= input.length) {
                fail("Expected character, but got EOF.")
            }
            return input[position]
        }

    /**
     * Returns the rest of the string being parsed, without advancing the parser position.
     */
    val rest: String
        get() {
            if (position >= input.length) {
                return ""
            }
            return input.substring(position)
        }

    /**
     * Current position of the parser.
     */
    var position: Int = 0

    /**
     * Matches the end of the input string.
     */
    fun eof() {
        if (position < input.length) {
            fail("Expected EOF, but got '$next'.")
        }
    }

    /**
     * Reads the next character in the input. If the parser is already at the end of the input, parsing fails.
     */
    fun char(): Char {
        if (position >= input.length) {
            fail("Expected character, but got EOF.")
        }
        val c = input[position]
        position += 1
        return c
    }

    /**
     * Reads the next `expected.length` characters from the input. Fails if they are not exactly [expected].
     */
    fun string(expected: String): String {
        if (position + expected.length > input.length) {
            fail("Expected '$expected', but got EOF.")
        }

        if (!input.regionMatches(position, expected, 0, expected.length)) {
            fail("Expected '$expected', but got '${input.substring(position, position + expected.length)}'.")
        }
        position += expected.length
        return expected
    }

    /**
     * Attempts to match [pattern] at the current position in the input, and returns the character sequence that
     * matches, if any. Fails if the pattern could not be matched at this position in the inut.
     */
    fun regex(pattern: Regex): String {
        val result = pattern.matchAt(input, position)?.value
            ?: fail("Expected pattern '$pattern', but there was no match.")
        position += result.length
        return result
    }

    /**
     * Unconditionally fail parsing with the given message.
     */
    fun fail(msg: String): Nothing {
        failException.reason = msg
        failException.position = position
        throw failException
    }

    /**
     * Propagate the last thrown parser failure.
     * If [newMsg] is not `null`, the failure's `reason` message is replaced by the given one.
     */
    fun propagateLastFailure(newMsg: String? = null): Nothing {
        if (newMsg != null) {
            failException.reason = newMsg
        }
        throw failException
    }

    /**
     * Reusable exception for backtracking. Speeds up backtracking by at least 10x compared to allocating a new one
     * every time.
     */
    private val failException: FailException = FailException("", 0)
}

/**
 * Used internally to signal parser failure, initiating backtracking.
 */
@PublishedApi
internal class FailException(var reason: String, var position: Int) : RuntimeException()
