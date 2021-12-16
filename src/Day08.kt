// https://adventofcode.com/2021/day/8
object Day08 : AdventOfCode, Solution by Omar_Miatello(
    day = 8,
    parser = { lines ->
        lines.map { line ->
            val (signalsString, digitsString) = line.split("|")
            signalsString.trim().split(" ") to digitsString.trim().split(" ")
        }
    },
    part1 = { input ->
        val validDigitsLength = listOf(1, 4, 7, 8).map { allDigits[it].length }
        input.sumOf { (_, digits) -> digits.count { it.length in validDigitsLength } }
    },
    testsPart1 = result(26),
    part2 = { input ->
        fun findBestConverters(
            converter: Converter?,
            sortedSignals: List<Pair<List<Int>, List<String>>>,
        ): List<Converter> {
            val (nums, potentialSignals) = sortedSignals.first()
            val potentials = nums.permutations().mapNotNull { currentNum ->
                potentialSignals.foldIndexed(converter) { idx, converter, signal ->
                    when (converter) {
                        is DigitReader -> converter
                        is Temp -> converter.tryWith(currentNum[idx], signal)
                        null -> null
                    }
                }
            }
            return if (sortedSignals.size > 1) {
                potentials.flatMap { findBestConverters(it, sortedSignals.drop(1)) }
            } else {
                potentials
            }
        }

        val lengthToIndexes = (0..9).groupBy { allDigits[it].length }
        input.sumOf { (signals, digits) ->
            val sortedSignals = signals
                .groupBy { lengthToIndexes.getValue(it.length) }
                .toList()
                .sortedBy { it.first.size }
            val digitReader = findBestConverters(Temp(), sortedSignals).first() as DigitReader
            digits.joinToString("") { digitReader.read(it).toString() }.toInt()
        }
    },
    testsPart2 = result(61229),
)

private fun <E> List<E>.permutations(): List<List<E>> {
    return when (size) {
        1 -> listOf(this)
        2 -> listOf(this, listOf(get(1), get(0)))
        3 -> listOf(
            this, listOf(get(0), get(2), get(1)),
            listOf(get(1), get(0), get(2)), listOf(get(1), get(2), get(0)),
            listOf(get(2), get(0), get(1)), listOf(get(2), get(1), get(0))
        )
        else -> error("too many!")
    }
}

private sealed interface Converter

@JvmInline
private value class Temp(
    private val segments: List<String> = allSegments.map { allSegments },
) : Converter {
    fun tryWith(num: Int, string: String): Converter? {
        val newConverter = segments.toMutableList()
        allDigits[num].booleans.forEachIndexed { index, shouldKeep ->
            newConverter[index] = newConverter[index].filter(if (shouldKeep) {
                { it in string }
            } else {
                { it !in string }
            })
        }
        return newConverter.takeIf { segment -> segment.all { it.isNotEmpty() } }?.let { segment ->
            if (segment.all { it.length == 1 }) {
                DigitReader(segment.withIndex().associate { (idx, segment) -> segment.first() to allSegments[idx] })
            } else {
                Temp(segment)
            }
        }
    }
}

@JvmInline
private value class DigitReader(
    private val converter: Map<Char, Char>,
) : Converter {
    fun read(digit: String): Int {
        val realDigit = digit.map { converter.getValue(it) }.sorted().joinToString("")
        return allDigitsStrings.indexOf(realDigit)
    }
}

private class N(str: String) {
    val length = str.length
    val booleans = allSegments.map { it in str }
}

private const val allSegments = "abcdefg"
private val allDigitsStrings = listOf(
    "abcefg",   // 0
    "cf",       // 1
    "acdeg",    // 2
    "acdfg",    // 3
    "bcdf",     // 4
    "abdfg",    // 5
    "abdefg",   // 6
    "acf",      // 7
    "abcdefg",  // 8
    "abcdfg",   // 9
)
private val allDigits = allDigitsStrings.map { N(it) }

