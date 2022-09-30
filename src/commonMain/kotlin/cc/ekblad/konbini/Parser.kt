package cc.ekblad.konbini

typealias Parser<T> = ParserState.() -> T

/**
 * The result of parsing a string.
 */
sealed class ParserResult<in T> {
    /**
     * Parsing succeeded, with the result stored in [result] and any remaining input stored in [remainingInput].
     */
    data class Ok<T>(val result: T, val remainingInput: String) : ParserResult<T>()

    /**
     * Parsing failed with the given [reason], at index [position] in the input string.
     */
    data class Error(
        val reason: String,
        val position: Int,
        val line: Int,
        val column: Int
    ) : ParserResult<Any?>()
}

/**
 * Applies the receiver parser to the given [input].
 * If [skipWhitespace] is true, any whitespace at the beginning of the input is skipped.
 */
fun <T> Parser<T>.parse(input: String, skipWhitespace: Boolean = false): ParserResult<T> =
    parse(input, skipWhitespace, ParserState())

/**
 * Like [parse], but with a custom parser state.
 */
fun <S : ParserState, T> Parser<T>.parse(input: String, skipWhitespace: Boolean = false, state: S): ParserResult<T> {
    state.input = input
    return try {
        val p = if (skipWhitespace) {
            parser { whitespace() ; this@parse() }
        } else {
            this@parse
        }
        ParserResult.Ok(state.p(), state.rest)
    } catch (e: FailException) {
        val consumedInput = input.substring(0, e.position)
        val line = consumedInput.count { it == '\n' }
        val column = e.position - consumedInput.lastIndexOf('\n')
        ParserResult.Error(e.reason, e.position, line + 1, column)
    }
}

/**
 * Applies the receiver parser to the given [input].
 * If [ignoreWhitespace] is true, any whitespace at the beginning or end of the input is ignored.
 */
fun <T> Parser<T>.parseToEnd(input: String, ignoreWhitespace: Boolean = false): ParserResult<T> =
    parseToEnd(input, ignoreWhitespace, ParserState())

/**
 * Like [parseToEnd], but with a custom parser state.
 */
fun <S : ParserState, T> Parser<T>.parseToEnd(
    input: String,
    ignoreWhitespace: Boolean = false,
    state: S
): ParserResult<T> {
    val p = parser {
        val result = this@parseToEnd()
        if (ignoreWhitespace) {
            whitespace()
        }
        eof()
        result
    }
    return p.parse(input, ignoreWhitespace, state)
}
