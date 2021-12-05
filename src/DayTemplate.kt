// https://adventofcode.com/2021/day/xx
fun dayExample() = adventOfCode(
    day = 0,
    parser = { input ->
        input.map { it.toInt() }
    },
    part1 = { parsed ->
        parsed.size
    },
    expectedTest1 = 1,
    part2 = { parsed ->
        parsed.size + 1
    },
    expectedTest2 = 2,
)