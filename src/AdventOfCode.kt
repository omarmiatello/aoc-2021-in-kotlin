import java.io.File

fun <R1 : Any, R2 : Any> adventOfCode(
    day: Int,
    part1: (List<String>) -> R1,
    expectedTest1: R1,
    part2: (List<String>) -> R2,
    expectedTest2: R2,
) {
    println("Day $day")

    on(filename = "Day${day}_part1_test") { input ->
        val output = part1(input)
        println("Part 1 (test) - expected value: $expectedTest1")
        check(output == expectedTest1) { "Something goes wrong, current result: $output" }
        output
    }

    on(filename = "Day${day}_part1") { input -> part1(input) }

    on(filename = "Day${day}_part2_test") { input ->
        val result = part2(input)
        println("Part 2 (test) - expected value: $expectedTest2")
        check(result == expectedTest2) { "Something goes wrong, current result: $result" }
        result
    }

    on(filename = "Day${day}_part2") { input -> part2(input) }
}

private fun on(
    filename: String,
    block: (List<String>) -> Any,
) {
    val input = File("data", "$filename.txt").readLines()
    val output = block(input)
    println("$filename output: $output")
    File("data", "${filename}_output.txt").writeText(output.toString())
}