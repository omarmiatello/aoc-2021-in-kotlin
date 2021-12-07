@file:OptIn(ExperimentalTime::class)

import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun <PARSED, RES1, RES2> adventOfCode(
    day: Int,
    parser: (List<String>) -> PARSED,
    part1: (PARSED) -> RES1,
    expectedTest1: RES1,
    part2: (PARSED) -> RES2,
    expectedTest2: RES2,
    filenamePart1: String = "Day${day}",
    filenamePart2: String = "Day${day}",
) {
    println("Day $day")
    on(filename = "${filenamePart1}_test", expected = expectedTest1) { part1(parser(it)) }
    on(filename = filenamePart1) { part1(parser(it)) }
    on(filename = "${filenamePart2}_test", expected = expectedTest2) { part2(parser(it)) }
    on(filename = filenamePart2) { part2(parser(it)) }
}

private fun <RES> on(
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