// https://adventofcode.com/2021/day/10
object Day10 : AdventOfCode, Solution by Omar_Miatello(
    day = 10,
    parser = { it },
    part1 = { input ->
        val charToPoints = listOf(3, 57, 1197, 25137)
            .mapIndexed { i, points -> Symbol.close[i] to points }
            .toMap()
        input.sumOf { line -> charToPoints[line.illegalChar] ?: 0 }
    },
    expectedTestPart1 = 26397,
    part2 = { input ->
        val charToPoints = Symbol.close.mapIndexed { i, c -> c to i + 1 }.toMap()
        input
            .mapNotNull { line ->
                line.missingChars?.let { (chars, sizes) ->
                    var points = 0L
                    chars.withIndex().reversed().forEach { (charIdx, c) ->
                        (1..sizes[charIdx]).forEach {
                            points = points * 5 + charToPoints.getValue(c)
                        }
                    }
                    points
                }
            }
            .sorted()
            .let { it[it.size / 2] }
    },
    expectedTestPart2 = 288957,
)

private object Symbol {
    private const val all = "([{<)]}>"
    val open = all.take(4)
    val close = all.drop(4)
    val openToClose = open.zip(close).toMap()
}

private sealed interface Issue

@JvmInline
private value class IllegalChar(val ic: Char) : Issue

@JvmInline
private value class MissingChars(val mc: Pair<List<Char>, List<Int>>) : Issue

private val String.missingChars get() = (findIssue() as? MissingChars)?.mc

private val String.illegalChar get() = (findIssue() as? IllegalChar)?.ic

private fun String.findIssue(): Issue? {
    val queue = mutableListOf<Char>()
    val sizes = mutableListOf<Int>()
    forEach { c ->
        if (c in Symbol.open) {
            if (c == queue.lastOrNull()) {
                sizes[sizes.lastIndex]++
            } else {
                queue += c
                sizes += 1
            }
        } else {
            if (c == Symbol.openToClose[queue.lastOrNull()]) {
                val newSize = --sizes[sizes.lastIndex]
                if (newSize == 0) {
                    queue.removeLast()
                    sizes.removeLast()
                }
            } else {
                return IllegalChar(c)
            }
        }
    }
    return if (queue.isNotEmpty()) {
        MissingChars(queue.map { Symbol.openToClose.getValue(it) } to sizes)
    } else null
}
