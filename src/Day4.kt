// https://adventofcode.com/2021/day/4
fun day4() = adventOfCode(
    day = 4,
    parser = { input ->
        SquidGame(
            random = input.first().toIntList(",").filterNotNull(),
            boards = input.drop(2)
                .windowed(5, 6)
                .map { Board(it.map { it.toIntList(" ") }) }
                .toMutableList(),
        )
    },
    part1 = { parsed ->
        var winner: Board? = null
        var n = 0
        var res = 0
        while (winner == null) {
            val r = parsed.random[n++]
            parsed.boards.forEach { it.remove(r) }
            winner = parsed.boards.firstOrNull { it.win() }
            if (winner != null) res = winner.points() * r
        }
        res
    },
    expectedTest1 = 4512,
    part2 = { parsed ->
        var res = 0
        parsed.random.forEach { r ->
            parsed.boards.forEach { it.remove(r) }
            val winner = parsed.boards.firstOrNull { it.win() }
            if (winner != null) {
                res = winner.points() * r
                parsed.boards.removeIf { it.win() }
            }
        }
        res
    },
    expectedTest2 = 1924,
)

class SquidGame(
    val random: List<Int>,
    val boards: MutableList<Board>,
)

private fun String.toIntList(delimiters: String): MutableList<Int?> = split(delimiters)
    .filter { it.isNotEmpty() }
    .map { it.toInt() }
    .toMutableList()

class Board(val items: List<MutableList<Int?>>) {
    fun win() = items.any { it.all { it == null } }
            || items.first().indices.any { idx -> items.all { it[idx] == null } }

    fun remove(n: Int) = items.forEach { it.replaceAll { if (it == n) null else it } }

    fun points() = items.sumOf { it.filterNotNull().sumOf { it } }
}
