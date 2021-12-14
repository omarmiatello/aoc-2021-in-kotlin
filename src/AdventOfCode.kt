@file:OptIn(ExperimentalTime::class)

import java.io.File
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

sealed interface AdventOfCode

fun interface Solution { fun launch() }

fun List<KClass<out AdventOfCode>>.launchAll() = forEach { (it.objectInstance as Solution).launch() }

fun <PARSED, RES1, RES2> Omar_Miatello(
    day: Int,
    parser: (List<String>) -> PARSED,
    part1: (PARSED) -> RES1,
    expectedTestPart1: RES1,
    part2: (PARSED) -> RES2,
    expectedTestPart2: RES2,
) = Solution {
    fun <RES> on(
        expected: RES? = null,
        block: () -> RES,
    ) {
        val (output, duration) = measureTimedValue { block() }
        val millis = duration.inWholeMilliseconds
        if (expected != null) {
            check(output == expected) { "[$millis ms] Something goes wrong! expected: $expected | current: $output" }
        } else {
            println("[${millis}ms]\t$output")
        }
    }

    println("Day $day")
    val parsedTest = parser(File("data", "Day${day}_test.txt").readLines())
    val parserDay = parser(File("data", "Day${day}.txt").readLines())
    on(expected = expectedTestPart1) { part1(parsedTest) }
    on { part1(parserDay) }
    on(expected = expectedTestPart2) { part2(parsedTest) }
    on { part2(parserDay) }
}