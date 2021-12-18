import kotlin.math.sqrt

// https://adventofcode.com/2021/day/17
object Day17 : AdventOfCode, Solution by Omar_Miatello(
    day = 17,
    parser = { lines -> "(-?\\d+)".toRegex().findAll(lines.first()).map { it.value.toInt() }.toList() },
    part1 = { (_, _, y1) ->
        val initialSpeedY = -y1 - 1
        initialSpeedY * (initialSpeedY + 1) / 2
    },
    testsPart1 = result(45),
    part2 = { (x1, x2, y1, y2) ->
        val targetX = x1..x2
        val targetY = y1..y2
        val initialSpeedX = sqrt(x1 * 2f).toInt()..x2
        val initialSpeedY = y1..-1 - y1
        fun hit(speedX: Int, speedY: Int, x: Int = 0, y: Int = 0): Boolean {
            val newX = x + speedX
            val newY = y + speedY
            return when {
                newX > x2 || newY < y1 -> false
                newX in targetX && newY in targetY -> true
                else -> hit(
                    speedX = (speedX - 1).coerceAtLeast(0),
                    speedY = speedY - 1,
                    x = newX,
                    y = newY,
                )
            }
        }
        initialSpeedX.sumOf { speedX ->
            initialSpeedY.count { speedY ->
                hit(speedX, speedY)
            }
        }
    },
    testsPart2 = result(112),
)