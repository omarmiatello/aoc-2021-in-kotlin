// https://adventofcode.com/2021/day/9
object Day09 : AdventOfCode, Solution by Omar_Miatello(
    day = 9,
    parser = { lines -> lines.map { line -> line.map { "$it".toInt() } } },
    part1 = { input ->
        fun getOrMax(x: Int, y: Int) = input.getOrNull(y)?.getOrNull(x) ?: Int.MAX_VALUE

        input.flatMapIndexed { y, row ->
            row.filterIndexed { x, n ->
                listOf(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1)
                    .map { (x1, y1) -> getOrMax(x1, y1) }
                    .all { n < it }
            }.map { it + 1 }
        }.sum()
    },
    testsPart1 = result(15),
    part2 = { input ->
        val booleans = input.map { it.map { it != 9 }.toMutableList() }
        fun getOrFalse(x: Int, y: Int) = booleans.getOrNull(y)?.getOrNull(x) ?: false
        fun findSize(x: Int, y: Int): Int = if (getOrFalse(x, y)) {
            booleans[y][x] = false
            1 + listOf(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1)
                .sumOf { (x1, y1) -> findSize(x1, y1) }
        } else 0

        val basinSizes = booleans.flatMapIndexed { y, row ->
            row.indices.map { x -> findSize(x, y) }
        }
        basinSizes.sortedDescending().take(3).reduce { acc, n -> acc * n }
    },
    testsPart2 = result(1134),
)