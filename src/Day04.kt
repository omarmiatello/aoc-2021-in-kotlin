// https://adventofcode.com/2021/day/4
object Day04 : AdventOfCode, Solution by Omar_Miatello(
    day = 4,
    parser = { lines ->
        SquidGame(
            random = lines.first().toIntList(",").filterNotNull(),
            boards = lines.drop(2)
                .windowed(5, 6)
                .map { Board(it.map { it.toIntList(" ") }) }
                .toMutableList(),
        )
    },
    part1 = { (random, boards) ->
        var winner: Board? = null
        var n = 0
        var res = 0
        while (winner == null) {
            val r = random[n++]
            boards.forEach { it.remove(r) }
            winner = boards.firstOrNull { it.win() }
            if (winner != null) res = winner!!.points() * r
        }
        res
    },
    testsPart1 = result(4512),
    part2 = { (random, boards) ->
        var res = 0
        random.forEach { r ->
            boards.forEach { it.remove(r) }
            val winner = boards.firstOrNull { it.win() }
            if (winner != null) {
                res = winner.points() * r
                boards.removeIf { it.win() }
            }
        }
        res
    },
    testsPart2 = result(1924),
)

private data class SquidGame(
    val random: List<Int>,
    val boards: MutableList<Board>,
)

private fun String.toIntList(delimiters: String): MutableList<Int?> = split(delimiters)
    .filter { it.isNotEmpty() }
    .map { it.toInt() }
    .toMutableList()

class Board(private val items: List<MutableList<Int?>>) {
    fun win() = items.any { item -> item.all { it == null } }
            || items.first().indices.any { idx -> items.all { it[idx] == null } }

    fun remove(n: Int) = items.forEach { item -> item.replaceAll { if (it == n) null else it } }

    fun points() = items.sumOf { item -> item.filterNotNull().sumOf { it } }
}