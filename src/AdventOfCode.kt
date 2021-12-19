@file:OptIn(ExperimentalTime::class)

import java.io.File
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

sealed interface AdventOfCode

fun interface Solution {
    fun launch()
}

fun List<KClass<out AdventOfCode>>.launchAll() = forEach { (it.objectInstance as Solution).launch() }

fun <PARSED, RES1, RES2> Omar_Miatello(
    day: Int,
    parser: (List<String>) -> PARSED,
    part1: (PARSED) -> RES1,
    testsPart1: (Int) -> List<Pair<List<String>, RES1>>,
    part2: (PARSED) -> RES2,
    testsPart2: (Int) -> List<Pair<List<String>, RES2>>,
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
    testsPart1(day).forEach { (inputTest, expectedTest) -> on(expected = expectedTest) { part1(parser(inputTest)) } }
    val parserDay = parser(File("data", "Day${day}.txt").readLines())
    on { part1(parserDay) }
    testsPart2(day).forEach { (inputTest, expectedTest) -> on(expected = expectedTest) { part2(parser(inputTest)) } }
    on { part2(parserDay) }
}

fun <RES> result(expected: RES): (Int) -> List<Pair<List<String>, RES>> = { day: Int ->
    listOf(File("data", "Day${day}_test.txt").readLines() to expected)
}

fun <RES> results(vararg inputToRes: Pair<List<String>, RES>): (Int) -> List<Pair<List<String>, RES>> = { inputToRes.toList() }
