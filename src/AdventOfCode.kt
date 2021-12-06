import java.io.File

fun <PARSED, RES1, RES2> adventOfCode(
    day: Int,
    parser: (List<String>) -> PARSED,
    part1: (PARSED) -> RES1,
    expectedTest1: RES1,
    part2: (PARSED) -> RES2,
    expectedTest2: RES2,
    filenamePart1: String = "Day${day}",
    filenamePart2: String = "Day${day}",
) {
    println("Day $day")

    on(filename = "${filenamePart1}_test") { input ->
        val output = part1(parser(input))
        println("Part 1 (test) - expected value: $expectedTest1")
        check(output == expectedTest1) { "Something goes wrong, current result: $output" }
        output
    }

    on(filename = filenamePart1) { input -> part1(parser(input)) }

    on(filename = "${filenamePart2}_test") { input ->
        val result = part2(parser(input))
        println("Part 2 (test) - expected value: $expectedTest2")
        check(result == expectedTest2) { "Something goes wrong, current result: $result" }
        result
    }

    on(filename = filenamePart2) { input -> part2(parser(input)) }
}

private fun <RES> on(
    filename: String,
    block: (List<String>) -> RES,
) {
    val input = File("data", "$filename.txt").readLines()
    val output = block(input)
    println("$filename output: $output")
    File("data", "${filename}_output.txt").writeText(output.toString())
}