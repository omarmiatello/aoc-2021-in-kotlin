// https://adventofcode.com/2021/day/6
object Day06 : AdventOfCode, Solution by Omar_Miatello(
    day = 6,
    parser = { lines -> lines.first().split(",").map { it.toInt() } },
    part1 = { input ->
        var fishes = input
        repeat(80) {
            fishes = fishes.flatMap { if (it > 0) listOf(it - 1) else listOf(6, 8) }
        }
        fishes.size
    },
    testsPart1 = result(5934),
    part2 = { input ->
        val cache = mutableMapOf<Pair<Int, Int>, Long>()
        fun countChildren(fish: Int, daysLeft: Int): Long = cache.getOrPut(daysLeft to fish) {
            val direct = (daysLeft + 6 - fish) / 7
            val indirect = (0 until direct).sumOf { sonIdx ->
                countChildren(fish = 8, daysLeft = daysLeft - fish - 1 - sonIdx * 7)
            }
            direct + indirect
        }
        input.size + input.sumOf { fish -> countChildren(fish = fish, daysLeft = 256) }
    },
    testsPart2 = result(26984457539),
)