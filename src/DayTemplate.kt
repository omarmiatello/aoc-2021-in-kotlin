/**
description
 */

fun dayExample() = adventOfCode(
    day = 0,
    part1 = { input ->
        input.size
    },
    expectedTest1 = 1,
    part2 = { input ->
        input.size + 1
    },
    expectedTest2 = 2,
)