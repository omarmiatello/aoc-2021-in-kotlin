// https://adventofcode.com/2021/day/2
fun day2() = adventOfCode(
    day = 2,
    part1 = { input ->
        input
            .map {
                val (direction, steps) = it.split(" ").run { get(0) to get(1).toInt() }
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
    part2 = { input ->
        var aim = 0
        var res = 0 to 0
        input.forEach {
            val (direction, steps) = it.split(" ").run { get(0) to get(1).toInt() }
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

