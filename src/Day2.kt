// https://adventofcode.com/2021/day/2
fun day2() = adventOfCode(
    day = 2,
    parser = { input ->
        input.map { it.split(" ").run { get(0) to get(1).toInt() } }
    },
    part1 = { parsed ->
        parsed
            .map { (direction, steps) ->
                when (direction) {
                    "forward" -> steps to 0
                    "up" -> 0 to -steps
                    "down" -> 0 to steps
                    else -> error("Unknown direction: $direction")
                }
            }
            .reduce { acc, pair -> acc.first + pair.first to acc.second + pair.second }
            .let { (x, y) -> x * y }
    },
    expectedTest1 = 150,
    part2 = { parsed ->
        var aim = 0
        var res = 0 to 0
        parsed.forEach { (direction, steps) ->
            when (direction) {
                "forward" -> res = res.first + steps to res.second + aim * steps
                "up" -> aim += -steps
                "down" -> aim += steps
                else -> error("Unknown direction: $direction")
            }
        }
        res.first * res.second
    },
    expectedTest2 = 900,
)

