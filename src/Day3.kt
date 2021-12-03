// https://adventofcode.com/2021/day/3
fun day3() = adventOfCode(
    day = 3,
    part1 = { input ->
        val parsed = input.map { it.map { it == '1' } }
        val gamma = parsed.mostCommon.toInt(valueFor1 = true)
        val epsilon = parsed.mostCommon.toInt(valueFor1 = false)
        gamma * epsilon
    },
    expectedTest1 = 198,
    part2 = { input ->
        val parsed = input.map { it.map { it == '1' } }
        val oxygen = findBest(
            initial = parsed,
            condition = { current, mostCommon -> current == mostCommon },
        ).toInt(valueFor1 = true)
        val co2 = findBest(
            initial = parsed,
            condition = { current, mostCommon -> current != mostCommon },
        ).toInt(valueFor1 = true)
        oxygen * co2
    },
    expectedTest2 = 230,
)

private val List<List<Boolean>>.mostCommon
    get() = first().indices.map { idx -> count { it[idx] } >= size / 2f }

private fun <T> List<T>.toInt(valueFor1: T) =
    joinToString("") { if (it == valueFor1) "1" else "0" }.toInt(2)

private fun findBest(
    initial: List<List<Boolean>>,
    condition: (current: Boolean, mostCommon: Boolean) -> Boolean,
): List<Boolean> {
    var res = initial
    var pos = 0
    while (res.size > 1) {
        val mostCommon = res.mostCommon[pos]
        res = res.filter { condition(it[pos], mostCommon) }
        pos++
    }
    return res.first()
}
