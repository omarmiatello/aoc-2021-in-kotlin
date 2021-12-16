// https://adventofcode.com/2021/day/11
object Day11 : AdventOfCode, Solution by Omar_Miatello(
    day = 11,
    parser = { lines -> lines.map { line -> line.map { "$it".toInt() } } },
    part1 = { input ->
        var flashes = 0
        input.loopUntil { loopIdx, frame ->
            flashes += frame.sumOf { row -> row.count { it == 0 } }
            loopIdx < 100
        }
        flashes
    },
    testsPart1 = result(1656),
    part2 = { input ->
        var all0Idx = 0
        input.loopUntil { loopIdx, frame ->
            val all0 = frame.all { row -> row.all { it == 0 } }
            all0Idx = loopIdx
            !all0
        }
        all0Idx
    },
    testsPart2 = result(195),
)

private val nearbyPos: List<Pair<Int, Int>> = (-1..1).flatMap { x -> (-1..1).map { y -> x to y } } - (0 to 0)

private fun List<List<Int>>.loopUntil(
    condition: (loopIdx: Int, frame: List<List<Int>>) -> Boolean,
) {
    var loopIndex = 0
    val frame = map { it.toMutableList() }
    while (condition(loopIndex++, frame)) {
        val willExplode = mutableSetOf<Pair<Int, Int>>()
        val exploded = mutableSetOf<Pair<Int, Int>>()
        frame.forEachIndexed { y, row ->
            row.indices.forEach { x ->
                if (++row[x] > 9) willExplode += x to y
            }
        }
        while (willExplode.isNotEmpty()) {
            willExplode.toList().forEach { (x, y) ->
                nearbyPos.forEach { (xNear, yNear) ->
                    val x1 = x + xNear
                    val y1 = y + yNear
                    val item = frame.getOrNull(y1)?.getOrNull(x1)
                    if (item != null && ++frame[y1][x1] > 9 && x1 to y1 !in exploded) {
                        willExplode += x1 to y1
                    }
                }
                willExplode -= x to y
                exploded += x to y
            }
        }
        exploded.forEach { (x, y) -> frame[y][x] = 0 }
    }
}
