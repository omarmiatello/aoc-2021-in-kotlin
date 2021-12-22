// https://adventofcode.com/2021/day/20
object Day20 : AdventOfCode, Solution by Omar_Miatello(
    day = 20,
    parser = { lines ->
        fun <T> Iterator<String>.nextBooleanLine(onLine: MutableList<T>.(List<Boolean>) -> Unit) = buildList {
            var completed = false
            while (!completed && hasNext()) {
                val line = next()
                if (line.isEmpty()) {
                    completed = true
                } else {
                    onLine(line.map { it == '#' })
                }
            }
        }

        lines.iterator().let { iterator ->
            ScannerImage(
                enhancementAlgorithm = iterator.nextBooleanLine { addAll(it) },
                image = Image(iterator.nextBooleanLine { add(it) }),
            )
        }
    },
    part1 = { (enhancementAlgorithm, image) ->
        image.enhance(enhancementAlgorithm, times = 2).litPixels
    },
    testsPart1 = result(35),
    part2 = { (enhancementAlgorithm, image) ->
        image.enhance(enhancementAlgorithm, times = 50).litPixels
    },
    testsPart2 = result(3351),
)

private data class ScannerImage(val enhancementAlgorithm: List<Boolean>, val image: Image)

private data class Image(val pixels: List<List<Boolean>>, val infinite: Boolean = false) {
    fun enhance(enhancementAlgorithm: List<Boolean>) = Image(
        pixels = (-1..pixels.size).map { y ->
            (-1..pixels.first().size).map { x ->
                enhancementAlgorithm[(-1..1).flatMap { y2 ->
                    (-1..1).map { x2 ->
                        pixels.getOrNull(y + y2)?.getOrNull(x + x2) ?: infinite
                    }
                }.joinToString("") { if (it) "1" else "0" }.toInt(2)]
            }
        },
        infinite = if (infinite) enhancementAlgorithm.last() else enhancementAlgorithm.first(),
    )

    fun enhance(
        enhancementAlgorithm: List<Boolean>,
        times: Int,
    ) = (1..times).fold(this) { acc, _ -> acc.enhance(enhancementAlgorithm) }

    val litPixels get() = pixels.sumOf { line -> line.count { it } }
}