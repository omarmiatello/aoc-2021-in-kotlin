import kotlin.math.abs

// https://adventofcode.com/2021/day/7
object Day07 : AdventOfCode, Solution by Omar_Miatello(
    day = 7,
    parser = { lines -> lines.first().split(",").map { it.toInt() } },
    part1 = { input ->
        (input.min..input.max).minOf { target ->
            input.sumOf { abs(it - target) }
        }
    },
    testsPart1 = result(37),
    part2 = { input ->
        (input.min..input.max).minOf { target ->
            input.sumOf {
                val distance = abs(it - target)
                distance * (distance + 1) / 2
            }
        }
    },
    testsPart2 = result(168),
)

private val List<Int>.max get() = maxOf { it }
private val List<Int>.min get() = minOf { it }