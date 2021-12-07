fun main() {
    AdventOfCode::class.sealedSubclasses.forEach { (it.objectInstance as Solution).launch() }
}
