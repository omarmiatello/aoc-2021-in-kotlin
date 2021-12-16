// https://adventofcode.com/2021/day/15
object Day15 : AdventOfCode, Solution by Omar_Miatello(
    day = 15,
    parser = { lines -> lines.mapIndexed { y, row -> row.mapIndexed { x, c -> Chiton(x, y, risk = "$c".toInt()) } } },
    part1 = { input -> input.findShorterPathDistance() },
    testsPart1 = result(40),
    part2 = { input ->
        val maxX = input.first().size
        val maxY = input.size
        (0 until maxY * 5).map { y ->
            (0 until maxX * 5).map { x ->
                val newRisk = input[y % maxY][x % maxX].risk + x / maxX + y / maxY
                Chiton(x, y, risk = newRisk % 10 + newRisk / 10)
            }
        }.findShorterPathDistance()
    },
    testsPart2 = result(315),
)

private data class Chiton(val x: Int, val y: Int, val risk: Int) {
    var totalRisk = Int.MAX_VALUE
    var estimated = Int.MAX_VALUE
}

private fun List<List<Chiton>>.findShorterPathDistance(): Int {
    val start = first().first().apply { totalRisk = 0 }
    val finish = last().last()
    val maxX = first().size - 1
    val maxY = size - 1
    val unknown = mutableSetOf(start)

    fun updateDistance(current: Chiton, neighbour: Chiton) {
        val score = current.totalRisk + neighbour.risk
        if (score < neighbour.totalRisk) {
            neighbour.totalRisk = score
            neighbour.estimated = score + maxX - neighbour.x + maxY - neighbour.y
            unknown += neighbour
        }
    }

    while (true) {
        val current = unknown.minByOrNull { it.estimated } ?: error("No path found, from $start to $finish")
        if (current != finish) {
            if (current.x < maxX) updateDistance(current, this[current.y][current.x + 1])
            if (current.y < maxY) updateDistance(current, this[current.y + 1][current.x])
            unknown -= current
        } else return finish.totalRisk
    }
}