// https://adventofcode.com/2021/day/1
object Day01 : AdventOfCode, Solution by Omar_Miatello(
    day = 1,
    parser = { lines -> lines.first().split(",").map { it.toInt() } },
    part1 = { input ->
        input.size
        1
    },
    testsPart1 = result(1),
    part2 = { input ->
        input.size + 1
        2
    },
    testsPart2 = result(2),
)