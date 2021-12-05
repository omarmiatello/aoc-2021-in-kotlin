// https://adventofcode.com/2021/day/1
fun day1() = adventOfCode(
    day = 1,
    parser = { input ->
        input.map { it.toInt() }
    },
    part1 = { parsed ->
        parsed
            .windowed(2)
            .count { it[1] > it[0] }
    },
    expectedTest1 = 7,
    part2 = { parsed ->
        parsed
            .windowed(3)
            .map { it.sum() }
            .windowed(2)
            .count { it[1] > it[0] }
    },
    expectedTest2 = 5,
)

