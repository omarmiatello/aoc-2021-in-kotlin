fun main() {
    val days = AdventOfCode::class.sealedSubclasses
    days.launchAll()
    days.toLinesOfCode().writeDocIndex()
}
