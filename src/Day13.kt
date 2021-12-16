// https://adventofcode.com/2021/day/13
object Day13 : AdventOfCode, Solution by Omar_Miatello(
    day = 13,
    parser = { lines ->
        lines.indexOf("").let { splitPos ->
            TransparentPaper(
                dots = lines.subList(0, splitPos).map { line ->
                    line.split(",")
                        .let { it[0].toInt() to it[1].toInt() }
                },
                foldList = lines.subList(splitPos + 1, lines.size).map { line ->
                    line.takeLastWhile { it != ' ' }
                        .split("=")
                        .let { (direction, n) ->
                            when (direction) {
                                "x" -> n.toInt() to 0
                                "y" -> 0 to n.toInt()
                                else -> error("Unknown direction $direction $n")
                            }
                        }
                },
            )
        }
    },
    part1 = { (dots, foldList) -> dots.foldInPosition(foldList.first()).size },
    testsPart1 = result(17),
    part2 = { (dots, foldList) ->
        val finalDots = foldList.fold(dots) { acc, foldIn -> acc.foldInPosition(foldIn) }
        printPaper(finalDots)
        0
    },
    testsPart2 = result(0),
)

private fun printPaper(dots: List<Pair<Int, Int>>) {
    (0..dots.maxOf { it.second }).forEach { y ->
        (0..dots.maxOf { it.first }).forEach { x ->
            print(if (x to y in dots) '#' else ' ')
        }
        println()
    }
}

private fun List<Pair<Int, Int>>.foldInPosition(
    foldIn: Pair<Int, Int>,
): List<Pair<Int, Int>> {
    val (foldX, foldY) = foldIn
    return if (foldX != 0) {
        map { (x, y) -> (if (x > foldX) 2 * foldX - x else x) to y }
    } else {
        map { (x, y) -> x to (if (y > foldY) 2 * foldY - y else y) }
    }.distinct()
}

private data class TransparentPaper(
    val dots: List<Pair<Int, Int>>,
    val foldList: List<Pair<Int, Int>>,
)