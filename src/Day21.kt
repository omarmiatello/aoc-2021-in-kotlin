// https://adventofcode.com/2021/day/21
object Day21 : AdventOfCode, Solution by Omar_Miatello(
    day = 21,
    parser = { lines -> lines.map { it.takeLastWhile { it.isDigit() }.toInt() } },
    part1 = { input ->
        var diceRoll = 0
        var diceValue = 0
        var currentPlayer = 0
        val space = input.toMutableList()
        val score = mutableListOf(0, 0)
        while (!score.any { it >= 1000 }) {
            diceRoll += 3
            space[currentPlayer] = (space[currentPlayer] + ++diceValue + ++diceValue + ++diceValue - 1) % 10 + 1
            score[currentPlayer] += space[currentPlayer]
            currentPlayer = (currentPlayer + 1) % 2
        }
        score[currentPlayer] * diceRoll
    },
    testsPart1 = result(739785),
    part2 = { input ->
        GameState(currentPlayer = 0, space = input, score = listOf(0, 0))
            .rollDiracDice()
            .results
            .toList()
            .maxOf { it }
    },
    testsPart2 = result(444356092776315L),
)

private val diracDice = (1..3).let { d -> d.flatMap { d1 -> d.flatMap { d2 -> d.map { d3 -> d1 + d2 + d3 } } } }

private val cache = mutableMapOf<Pair<List<Int>, Int>, GameResult>()

private operator fun Pair<Long, Long>.plus(other: Pair<Long, Long>) = first + other.first to second + other.second

private class GameResult(val results: Pair<Long, Long>)

private class GameState(private val state: List<Int>) {
    constructor(
        currentPlayer: Int,
        space: List<Int>,
        score: List<Int>,
    ) : this(listOf(currentPlayer) + space + score)

    fun rollDiracDice(): GameResult = diracDice
        .fold(GameResult(0L to 0L)) { result, dice ->
            val roll = cache.getOrPut(state to dice) {
                val gameState = GameState(
                    state.toMutableList().also {
                        val currentPlayer = it[CURRENT_PLAYER]
                        val newSpace = (it[indexForSpace(currentPlayer)] + dice - 1) % 10 + 1
                        it[CURRENT_PLAYER] = (currentPlayer + 1) % 2
                        it[indexForSpace(currentPlayer)] = newSpace
                        it[indexForScore(currentPlayer)] += newSpace
                    }
                )
                gameState.getWinner()
                    .takeIf { it.results.first != 0L || it.results.second != 0L } ?: gameState.rollDiracDice()
            }
            GameResult(result.results + roll.results)
        }

    fun getWinner(): GameResult = when {
        state[SCORE_1] >= WINNING_SCORE -> GameResult(1L to 0L)
        state[SCORE_2] >= WINNING_SCORE -> GameResult(0L to 1L)
        else -> GameResult(0L to 0L)
    }

    companion object {
        private const val WINNING_SCORE = 21
        private const val CURRENT_PLAYER = 0
        private const val SCORE_1 = 3
        private const val SCORE_2 = 4
        private fun indexForSpace(currentPlayer: Int) = currentPlayer + 1
        private fun indexForScore(currentPlayer: Int) = currentPlayer + 3
    }
}