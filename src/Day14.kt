// https://adventofcode.com/2021/day/14
object Day14 : AdventOfCode, Solution by Omar_Miatello(
    day = 14,
    parser = { lines ->
        SubmarinePolymerization(
            initialValue = lines.first(),
            converter = lines.drop(2)
                .map { it.split(" -> ").let { it[0] to it[1].first() } }
                .groupBy({ it.first.first() }) { it.first[1] to it.second }
                .mapValues { it.value.toMap() },
        )
    },
    part1 = { (initialValue, converter) ->
        (1..10)
            .fold(initialValue.toList()) { acc, _ ->
                acc.toList()
                    .windowed(2) { (c1, c2) -> listOf(c1, converter.convert(c1, c2)) }
                    .flatten() + acc.last()
            }
            .groupingBy { it }
            .eachCount()
            .values
            .sorted()
            .let { counter -> counter.last() - counter.first() }
    },
    expectedTestPart1 = 1588,
    part2 = { (initialValue, converter) ->
        (1..40)
            .fold(initialValue.toList().windowed(2).map { (c1, c2) -> (c1 to c2) to 1L }.sum()) { acc, _ ->
                acc.flatMap { (pair, n) ->
                    val (c1, c2) = pair
                    val x = converter.convert(c1, c2)
                    listOf((c1 to x) to n, (x to c2) to n)
                }.sum()
            }
            .map { (pair, n) -> pair.first to n }
            .groupBy({ it.first }) { it.second }
            .mapValues { it.value.sum() }
            .toMutableMap()
            .also { map -> map[initialValue.last()] = map.getValue(initialValue.last()) + 1 }
            .values
            .sorted()
            .let { counter -> counter.last() - counter.first() }
    },
    expectedTestPart2 = 2188189693529,
)


data class SubmarinePolymerization(
    val initialValue: String,
    val converter: Map<Char, Map<Char, Char>>,
)

private fun Map<Char, Map<Char, Char>>.convert(c1: Char, c2: Char) = getValue(c1).getValue(c2)

private fun List<Pair<Pair<Char, Char>, Long>>.sum() = groupBy({ it.first }) { it.second }
    .mapValues { it.value.sum() }
    .toList()