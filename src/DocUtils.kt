import java.io.File
import kotlin.reflect.KClass

data class LinesOfCode(val day: Int, val inputTest: String, val inputDay: String, val source: String)

fun List<KClass<out AdventOfCode>>.toLinesOfCode() = map {
    val day = it.simpleName!!.drop(3).toInt()
    LinesOfCode(
        day,
        File("data", "Day${day}_test.txt").readText(),
        File("data", "Day$day.txt").readText(),
        File("src", "Day${day.toString().padStart(2, '0')}.kt").readText(),
    )
}

fun List<LinesOfCode>.writeDocIndex() {
    fun String.cleanForHtml() = replace(">", "&gt;")
        .replace("<", "&lt;")
        .replace("\"", "&quot;")

    val templateIndex = File("templates", "docs_index.html").readText()
    val templateItem = File("templates", "docs_index_item.html").readText()

    File("docs", "index.html").writeText(
        templateIndex.replace("<!--items-->", joinToString("\n") {
            templateItem
                .replace("<!--source-->", it.source.cleanForHtml())
                .replace("<!--dayn-->", it.day.toString())
                .replace("<!--day0n-->", it.day.toString().padStart(2, '0'))
                .replace("<!--inputTest-->", it.inputTest.cleanForHtml())
                .replace("<!--inputDay-->", it.inputDay.cleanForHtml())
        })
    )
}
