# Konbini
[![Docs](https://img.shields.io/badge/docs-latest-informational)](http://valderman.github.io/konbini/konbini/cc.ekblad.konbini/)
[![Release](https://jitpack.io/v/cc.ekblad/konbini.svg)](https://jitpack.io/#cc.ekblad/konbini)
![Build Status](https://github.com/valderman/konbini/workflows/CI/badge.svg)
[![License](https://img.shields.io/github/license/valderman/konbini)](https://github.com/valderman/konbini/blob/main/LICENSE)

A Kotlin multiplatform parser combinator library.

## Getting started
### Adding a Dependency on Konbini
Konbini is hosted on Jitpack, so you need to first att the Jitpack repository
to your build file, then add a dependency on Konbini.

For `build.gradle.kts` (Kotlin DSL):
```kotlin
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("cc.ekblad.konbini:konbini:0.1.1")
}
```

### A Simple Parser
Konbini parsers are constructed from _combinators_: tiny functions which
glue together other tiny functions into complex parsers.

Consider the following parser which parses and computes expressions containing
integer literals and additions:
```kotlin
import cc.ekblad.konbini.Parser
import cc.ekblad.konbini.char
import cc.ekblad.konbini.integer
import cc.ekblad.konbini.oneOf
import cc.ekblad.konbini.parser
import cc.ekblad.konbini.whitespace

val addition: Parser<Long> = parser {
    val lhs = expr()
    whitespace()
    char('+')
    whitespace()
    val rhs = expr()
    return lhs + rhs
}
val expr: Parser<Long> = oneOf(integer, addition)
```

This example makes use five basic combinators. Three of them are leaf parsers,
or atoms:
- `integer`, which parses any integer that can fits in a 64-bit signed `Long`;
- `char`, which parses any one out of zero or more characters passed to it as arguments; and
- `whitespace`, which parses zero or more whitespace characters.

The remaining two are more interesting:
- `parser` lets you combine parsers into bigger parsers.
  To use a parser from within a `parser` block, simply call it as a normal function.
- `oneOf` takes zero or more parsers as arguments, and tries them all from left to right
  until it finds one that succeeds. If none of the given parsers succeed, the `oneOf` parser fails.
  Any parser can be passed as an argument to `oneOf`. Unlike many other parser generators and libraries
  you may be familiar with, such as [ANTLR](https://www.antlr.org) or [GNU Bison](https://www.gnu.org/software/bison/),
  Konbini implements arbitrary backtracking.

In addition to these, Konbini defines several other helpful parser combinators to quickly get your parser
off the ground.
- `regex` matches regular expressions.
- `doubleQuotedString` and `singleQuotedString` matches quoted strings, including escape code handling.
- `chainl` and `chainr` make it easy to define left-recursive and right-recursive parsers respectively.
- Fore more information about the combinators available out of the box,
  see [the API documentation](https://valderman.github.io/konbini/konbini/cc.ekblad.konbini/).

### Running Your Parser
Each Konbini parser has two extension methods which lets you apply them to arbitrary strings.
- `parse` applies the parser to a string, reading as much of the string as it can, and returns both the result
  and any remaining input.
- `parseToEnd` does the same as `parse`, but returns an error if the parser did not match the _entire_ input string.

Both methods can be configured to ignore whitespace at the start and end of its input for convenience.

### A More Complex Parser
Unlike our simple example parser, most real parsers don't calculate values on the fly; they build up a syntax tree
of some kind. For a more realistic example, the following parser uses mostly built-in functionality to implement
a complete JSON parser.

```kotlin
val comma = parser { whitespace() ; char(',') ; whitespace() }
val colon = parser { whitespace() ; char(':') ; whitespace() }
val pKeyValue = parser { doubleQuotedString().also { colon() } to pValue() }
val pAtom = oneOf(decimal, doubleQuotedString, boolean, string("null").map { null })
val pArray = bracket(
    parser { char('[') ; whitespace() },
    parser { whitespace() ; char(']') },
    parser { chain(pValue, comma).terms },
)
val pDict = bracket(
    parser { char('{') ; whitespace() },
    parser { whitespace() ; char('}') },
    parser { chain(pKeyValue, comma).terms.toMap() },
)
val pValue: Parser<Any?> = oneOf(pAtom, pDict, pArray)
```

This parser is capable of processing around 35 MB of JSON per second on an Apple M2; about the same
speed as the parser used for the [better-parse](https://github.com/h0tk3y/better-parse) benchmark.
