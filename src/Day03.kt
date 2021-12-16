// https://adventofcode.com/2021/day/3
object Day03 : AdventOfCode, Solution by Omar_Miatello(
    day = 3,
    parser = { lines -> lines.map { line -> line.map { it == '1' } } },
    part1 = { input ->
        val gamma = input.commonsByIndex.toInt(valueFor1 = true)
        val epsilon = input.commonsByIndex.toInt(valueFor1 = false)
        gamma * epsilon
    },
    testsPart1 = result(198),
    part2 = { input ->
        fun findBest(
            initial: List<List<Boolean>>,
            condition: (current: Boolean, common: Boolean) -> Boolean,
        ): Int {
            var res = initial
            var pos = 0
            while (res.size > 1) {
                val common = res.commonsByIndex[pos]
                res = res.filter { condition(it[pos], common) }
                pos++
            }
            return res.first().toInt(valueFor1 = true)
        }

        val oxygen = findBest(
            initial = input,
            condition = { current, common -> current == common },
        )
        val co2 = findBest(
            initial = input,
            condition = { current, common -> current != common },
        )
        oxygen * co2
    },
    testsPart2 = result(230),
)

private val List<List<Boolean>>.commonsByIndex: List<Boolean>
    get() = first().indices.map { idx -> count { it[idx] } >= size / 2f }

private fun <T> List<T>.toInt(valueFor1: T): Int =
    joinToString("") { if (it == valueFor1) "1" else "0" }.toInt(2)