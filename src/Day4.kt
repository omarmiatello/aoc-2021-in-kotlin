// https://adventofcode.com/2021/day/4
fun day4() = adventOfCode(
    day = 4,
    part1 = { input ->
        val random = input.toRandom()
        val boards = input.toBoards()
        var winner: Board? = null
        var n = 0
        var res = 0
        while (winner == null) {
            val r = random[n++]
            boards.forEach { it.remove(res) }
            winner = boards.firstOrNull { it.win() }
            if (winner != null) res = winner.points() * r
        }
        res
    },
    expectedTest1 = 4512,
    part2 = { input ->
        val random = input.toRandom()
        val boards = input.toBoards().toMutableList()
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
    expectedTest2 = 1924,
)

private fun List<String>.toRandom() = first().toIntList(",").filterNotNull()

private fun List<String>.toBoards() = drop(2)
    .windowed(5, 6)
    .map { Board(it.map { it.toIntList(" ") }) }

private fun String.toIntList(delimiters: String): MutableList<Int?> = split(delimiters)
    .filter { it.isNotEmpty() }
    .map { it.toInt() }
    .toMutableList()

class Board(val items: List<MutableList<Int?>>) {
    fun win() = items.any { it.all { it == null } } || items.first().indices.any { idx -> items.all { it[idx] == null } }
    fun remove(n: Int) = items.forEach { it.replaceAll { if (it == n) null else it } }
    fun points() = items.sumOf { it.filterNotNull().sumOf { it } }
}
