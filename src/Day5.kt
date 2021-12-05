import kotlin.math.abs
import kotlin.math.sign

// https://adventofcode.com/2021/day/5
fun day5() = adventOfCode(
    day = 5,
    part1 = { input ->
        val parsed = parse(input).filter { it.start.x == it.end.x || it.start.y == it.end.y }
        val allPoints = mutableSetOf<Pair<Int, Int>>()
        val doublePoints = mutableSetOf<Pair<Int, Int>>()
        parsed.forEach { it.points.forEach { p -> if (!allPoints.add(p)) doublePoints.add(p) } }
        doublePoints.size
    },
    expectedTest1 = 5,
    part2 = { input ->
        val parsed = parse(input).filter {
            it.start.x == it.end.x
                    || it.start.y == it.end.y
                    || abs(it.start.x - it.end.x) == abs(it.start.y - it.end.y)
        }
        val allPoints = mutableSetOf<Pair<Int, Int>>()
        val doublePoints = mutableSetOf<Pair<Int, Int>>()
        parsed.forEach { it.points.forEach { p -> if (!allPoints.add(p)) doublePoints.add(p) } }
        doublePoints.size
    },
    expectedTest2 = 12,
)

private fun parse(input: List<String>): List<Line> {
    return input.map { it.split(" -> ").map { it.split(",").map { it.toInt() } } }
        .map { Line(start = Dot(it[0][0], it[0][1]), end = Dot(it[1][0], it[1][1])) }
}

class Line(
    val start: Dot,
    val end: Dot,
) {
    private val signs = Dot(sign(end.x - start.x), sign(end.y - start.y))
    private val distance = abs(if (start.x != end.x) end.x - start.x else end.y - start.y)
    val points = (0..distance).map { d -> start.x + (d * signs.x) to start.y + (d * signs.y) }
}

class Dot(val x: Int, val y: Int)

fun sign(x: Int) = sign(x.toDouble()).toInt()