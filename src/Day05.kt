import kotlin.math.abs
import kotlin.math.sign

// https://adventofcode.com/2021/day/5
object Day05 : AdventOfCode, Solution by Omar_Miatello(
    day = 5,
    parser = { lines ->
        lines.map { it.split(" -> ").map { it.split(",").map { it.toInt() } } }
            .map { Line(start = Dot(it[0][0], it[0][1]), end = Dot(it[1][0], it[1][1])) }
    },
    part1 = { input ->
        input
            .filter { it.start.x == it.end.x || it.start.y == it.end.y }
            .findDoublePoints()
            .size
    },
    testsPart1 = result(5),
    part2 = { input ->
        input
            .filter {
                it.start.x == it.end.x
                        || it.start.y == it.end.y
                        || abs(it.start.x - it.end.x) == abs(it.start.y - it.end.y)
            }
            .findDoublePoints()
            .size
    },
    testsPart2 = result(12),
)

private fun List<Line>.findDoublePoints(): Set<Pair<Int, Int>> {
    val allPoints = mutableSetOf<Pair<Int, Int>>()
    val doublePoints = mutableSetOf<Pair<Int, Int>>()
    forEach { it.points.forEach { p -> if (!allPoints.add(p)) doublePoints.add(p) } }
    return doublePoints
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