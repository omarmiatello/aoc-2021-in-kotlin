import kotlin.math.abs

// https://adventofcode.com/2021/day/7
fun day7() = adventOfCode(
    day = 7,
    parser = { input ->
        input.first().split(",").map { it.toInt() }
    },
    part1 = { parsed ->
        (parsed.min..parsed.max)
            .minOf { target ->
                parsed.sumOf { abs(it - target) }
            }
    },
    expectedTest1 = 37,
    part2 = { parsed ->
        (parsed.min..parsed.max)
            .minOf { target ->
                parsed.sumOf {
                    val distance = abs(it - target)
                    distance * (distance + 1) / 2
                }
            }
    },
    expectedTest2 = 168,
)

private val List<Int>.max get() = maxOf { it }
private val List<Int>.min get() = minOf { it }