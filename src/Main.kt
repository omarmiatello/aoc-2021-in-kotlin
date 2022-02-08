fun main() {
    val days = AdventOfCode::class.sealedSubclasses.takeLast(1)
    days.launchAll()
    days.toLinesOfCode().writeDocIndex()
}