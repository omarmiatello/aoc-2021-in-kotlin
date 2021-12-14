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
        val cache = mutableMapOf<Triple<Char, Char, Int>, Map<Char, Long>>()
        fun charCounter(c1: Char, c2: Char, steps: Int): Map<Char, Long> = cache.getOrPut(Triple(c1, c2, steps)) {
            if (steps == 1) buildMap {
                inc(converter.convert(c1, c2))
                inc(c1)
                inc(c2)
            } else buildMap {
                val x = converter.convert(c1, c2)
                charCounter(c1, x, steps - 1).forEach { (c, n) -> inc(c, n) }
                charCounter(x, c2, steps - 1).forEach { (c, n) -> inc(c, n) }
                dec(x)
            }
        }

        initialValue.toList()
            .windowed(2) { (c1, c2) -> charCounter(c1, c2, 40).mutate { it.dec(c2) } }
            .reduce { acc, map -> acc.mutate { map.forEach { (c, n) -> it.inc(c, n) } } }
            .let { map -> map.mutate { it.inc(initialValue.last()) } }
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
private fun Map<Char, Long>.mutate(block: (MutableMap<Char, Long>) -> Unit) = toMutableMap().also(block)
private fun MutableMap<Char, Long>.inc(c3: Char, n: Long = 1) = put(c3, getOrElse(c3) { 0L } + n)
private fun MutableMap<Char, Long>.dec(c3: Char, n: Long = 1) = put(c3, getOrElse(c3) { 0L } - n)