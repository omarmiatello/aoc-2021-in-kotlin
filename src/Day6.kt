// https://adventofcode.com/2021/day/6

fun day6() = adventOfCode(
    day = 6,
    parser = { input ->
        input.first().split(",").map { it.toInt() }
    },
    part1 = { parsed ->
        var fishes = parsed
        repeat(80) {
            fishes = fishes.flatMap { if (it > 0) listOf(it - 1) else listOf(6, 8) }
        }
        fishes.size
    },
    expectedTest1 = 5934,
    part2 = { parsed ->
        val cache = mutableMapOf<Pair<Int, Int>, Long>()
        fun countChildren(fish: Int, daysLeft: Int): Long = cache.getOrPut(daysLeft to fish) {
            val direct = (daysLeft + 6 - fish) / 7
            val indirect = (0 until direct).sumOf { sonIdx ->
                countChildren(fish = 8, daysLeft = daysLeft - fish - 1 - sonIdx * 7)
            }
            direct + indirect
        }
        parsed.size + parsed.sumOf { fish -> countChildren(fish = fish, daysLeft = 256) }
    },
    expectedTest2 = 26984457539,
)