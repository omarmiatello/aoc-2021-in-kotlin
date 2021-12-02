// https://adventofcode.com/2021/day/1
fun day1() = adventOfCode(
    day = 1,
    part1 = { input ->
        var res = 0
        input.map { it.toInt() }
            .reduce { acc, i ->
                if (i > acc) res++
                i
            }
        res
    },
    expectedTest1 = 7,
    part2 = { input ->
        var res = 0
        input.map { it.toInt() }
            .windowed(3)
            .map { it.sum() }
            .reduce { acc, i ->
                if (i > acc) res++
                i
            }
        res
    },
    expectedTest2 = 5,
)

