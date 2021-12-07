@file:OptIn(ExperimentalTime::class)

import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

sealed interface AdventOfCode

fun interface Solution { fun launch() }

fun <PARSED, RES1, RES2> Omar_Miatello(
    day: Int,
    parser: (List<String>) -> PARSED,
    part1: (PARSED) -> RES1,
    expectedTestPart1: RES1,
    part2: (PARSED) -> RES2,
    expectedTestPart2: RES2,
    filenamePart1: String = "Day${day}",
    filenamePart2: String = "Day${day}",
) = Solution {
    fun <RES> on(
        filename: String,
        expected: RES? = null,
        block: (List<String>) -> RES,
    ) {
        val input = File("data", "$filename.txt").readLines()
        val (output, duration) = measureTimedValue { block(input) }
        val millis = duration.inWholeMilliseconds
        if (expected != null) {
            check(output == expected) { "[$millis ms] Something goes wrong! expected: $expected | current: $output" }
        } else {
            println("[${millis}ms]\t$output")
        }
    }

    println("Day $day")
    on(filename = "${filenamePart1}_test", expected = expectedTestPart1) { part1(parser(it)) }
    on(filename = filenamePart1) { part1(parser(it)) }
    on(filename = "${filenamePart2}_test", expected = expectedTestPart2) { part2(parser(it)) }
    on(filename = filenamePart2) { part2(parser(it)) }
}