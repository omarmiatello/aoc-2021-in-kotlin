// https://adventofcode.com/2021/day/1
object Day01 : AdventOfCode, Solution by Omar_Miatello(
    day = 1,
    parser = { lines -> lines.map { it.toInt() } },
    part1 = { input ->
        input
            .windowed(2)
            .count { it[1] > it[0] }
    },
    expectedTestPart1 = 7,
    part2 = { input ->
        input
            .windowed(3)
            .map { it.sum() }
            .windowed(2)
            .count { it[1] > it[0] }
    },
    expectedTestPart2 = 5,
)