@file:Suppress("NOTHING_TO_INLINE")
package cc.ekblad.konbini

/**
 * A parsed chain of elements, and the separators separating each element of the chain.
 *
 * Elements `term[0]` and `term[1]` are separated by `separators[0]`,
 * `term[1]` and `term[2]` are separated by `separators[1]`, etc.
 * It follows that [separators] will always have one element more than [terms], unless the chain is empty, in which case
 * both will be empty.
 */
data class Chain<T, S>(val terms: List<T>, val separators: List<S>) {
    companion object {
        private val emptyChain: Chain<*, *> = Chain<Any?, Any?>(emptyList(), emptyList())

        @Suppress("UNCHECKED_CAST")
        fun <T, S> empty(): Chain<T, S> = emptyChain as Chain<T, S>
    }
}

/**
 * Applies the given function to the result of the receiver parser if it succeeds.
 */
inline fun <T, U> Parser<T>.map(crossinline f: (T) -> U): Parser<U> =
    parser { f(this.this@map()) }

/**
 * Applies the receiver parser, then applies [p].
 * This parser is not atomic.
 */
inline fun <T, U> Parser<T>.then(crossinline p: Parser<U>): Parser<Pair<T, U>> =
    parser { this@then() to p() }

/**
 * Create a parser that performs the given parsing computation.
 *
 * If a sub-parser used within the given parsing computation fails, the entire `parser` call fails.
 * However, this failure is not atomic, in that any sub-parser that succeeded before the failing sub-parser
 * will still have succeeded. This means that if the failure is caught further up the parsing tree, backtracking
 *  will resume from the position just before the failing sub-parser. NOT before the entire `parser` call.
 *
 * For atomic parser failure, use [atomically] instead.
 */
fun <T> parser(p: ParserState.() -> T): Parser<T> = p

/**
 * Parses zero or more instances of [p].
 * As this parser potentially matches zero elements, it will never fail.
 */
inline fun <T> ParserState.many(crossinline p: Parser<T>): List<T> {
    val list = mutableListOf<T>()
    var savedPos = 0
    while (true) {
        try {
            savedPos = position
            list.add(p())
        } catch (e: FailException) {
            position = savedPos
            return list
        }
    }
}

/**
 * Creates a [ParserState.many] parser.
 */
inline fun <T> many(crossinline p: Parser<T>) = parser { many(p) }

/**
 * Atomically parses one or more instances of [p].
 */
inline fun <T> ParserState.many1(crossinline p: Parser<T>): List<T> = atomically {
    val elems = many(p)
    if (elems.isEmpty()) {
        fail("Expected at least one element, but got none.")
    }
    elems
}

/**
 * Creates a [ParserState.many1] parser.
 */
inline fun <T> many1(noinline p: Parser<T>) = parser { many1(p) }

/**
 * Tries the given parsers in order, returning the result of the first one to succeed.
 * Fails if none of the given parsers succeed.
 */
inline fun <T> ParserState.oneOf(vararg ps: Parser<T>): T {
    val savedPos = position
    for (p in ps) {
        try {
            return p()
        } catch (e: FailException) {
            /* Try the next one */
            position = savedPos
        }
    }
    val alts = ps.joinToString { it.toString() }
    fail("Expected one of $alts, but none of them matched.")
}

/**
 * Creates a [ParserState.oneOf] parser.
 */
inline fun <T> oneOf(vararg ps: Parser<T>) = parser { oneOf(*ps) }

/**
 * Parses one or more instances of [p], separated by [separator], and returns the elements with their respective
 * separators.
 */
inline fun <T, S> ParserState.chain1(crossinline p: Parser<T>, crossinline separator: Parser<S>): Chain<T, S> =
    atomically {
        val head = p()
        val tail = many {
            separator() to p()
        }
        val separators = mutableListOf<S>()
        val terms = mutableListOf<T>(head)
        for ((s, t) in tail) {
            separators.add(s)
            terms.add(t)
        }
        Chain(terms, separators)
    }

/**
 * Creates a [ParserState.chain1] parser.
 */
inline fun <T, S> chain1(crossinline p: Parser<T>, crossinline separator: Parser<S>) =
    parser { chain1(p, separator) }

/**
 * Parses zero or more instances of [p], separated by [separator], and returns the elements with their respective
 * separators.
 * As this parser potentially matches zero elements, it will never fail.
 */
inline fun <T, S> ParserState.chain(crossinline p: Parser<T>, crossinline separator: Parser<S>): Chain<T, S> =
    oneOf(
        parser { chain1(p, separator) },
        parser { Chain.empty() }
    )

/**
 * Creates a [ParserState.chain] parser.
 */
inline fun <T, S> chain(crossinline p: Parser<T>, crossinline separator: Parser<S>) =
    parser { chain(p, separator) }

/**
 * Parses one or more instances of [p], separated by [separator], and combines the elements left-to-right using
 * the given [combine] function.
 */
inline fun <T, S> ParserState.chainl(
    crossinline p: Parser<T>,
    crossinline separator: Parser<S>,
    crossinline combine: (T, T, S) -> T
): T {
    val c = chain(p, separator)
    var result: T = c.terms.firstOrNull()
        ?: fail("Chain did not match any elements.")
    for (i in c.separators.indices) {
        result = combine(result, c.terms[i + 1], c.separators[i])
    }
    return result
}

/**
 * Creates a [ParserState.chainl] parser.
 */
inline fun <T, S> chainl(
    crossinline p: Parser<T>,
    crossinline separator: Parser<S>,
    crossinline combine: (T, T, S) -> T
) = parser { chainl(p, separator, combine) }

/**
 * Parses one or more instances of [p], separated by [separator], and combines the elements right-to-left using
 * the given [combine] function.
 */
inline fun <T, S> ParserState.chainr(
    crossinline p: Parser<T>,
    crossinline separator: Parser<S>,
    crossinline combine: (T, T, S) -> T
): T {
    val c = chain(p, separator)
    var result: T = c.terms.lastOrNull()
        ?: fail("Chain did not match any elements.")
    for (i in (c.separators.size - 1) downTo 0) {
        result = combine(c.terms[i], result, c.separators[i])
    }
    return result
}

/**
 * Creates a [ParserState.chainr] parser.
 */
inline fun <T, S> chainr(
    crossinline p: Parser<T>,
    crossinline separator: Parser<S>,
    crossinline combine: (T, T, S) -> T
) = parser { chainr(p, separator, combine) }

/**
 * Parses [p], requiring it to be preceded and followed by the [before] and [after] chars respectively.
 * If any part of the parser fails, the entire parser fails.
 * Returns the result of [p].
 */
inline fun <B, T> ParserState.bracket(
    crossinline before: Parser<B>,
    crossinline after: Parser<B>,
    crossinline p: Parser<T>
): T = atomically {
    before()
    val x = p()
    after()
    x
}

/**
 * Creates a [ParserState.bracket] parser.
 */
inline fun <B, T> bracket(
    crossinline before: Parser<B>,
    crossinline after: Parser<B>,
    crossinline p: Parser<T>
) = parser { bracket(before, after, p) }

/**
 * Executes the given parser atomically. The parser either succeeds, or the entire parser fails.
 */
inline fun <T> ParserState.atomically(p: Parser<T>): T {
    val savedPos = position
    return try {
        p()
    } catch (e: FailException) {
        position = savedPos
        e.position = savedPos
        throw e
    }
}

/**
 * Creates an [atomically] parser.
 */
inline fun <T> atomically(crossinline p: Parser<T>): Parser<T> =
    parser { atomically(p) }

/**
 * Executes the given parser atomically. If it succeeds, the result of [p] is returned. If it fails, `null` is returned.
 */
inline fun <T> ParserState.tryParse(p: Parser<T>): T? {
    val savedPos = position
    return try {
        p()
    } catch (e: FailException) {
        position = savedPos
        e.position = savedPos
        return null
    }
}

/**
 * Creates a [tryParse] parser.
 */
inline fun <T> tryParse(crossinline p: Parser<T>): Parser<T?> =
    parser { tryParse(p) }
