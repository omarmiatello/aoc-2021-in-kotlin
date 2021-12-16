// https://adventofcode.com/2021/day/12
object Day12 : AdventOfCode, Solution by Omar_Miatello(
    day = 12,
    parser = { lines -> lines.map { it.split("-") } },
    part1 = { input ->
        val nodesMap = input.toNodesMap()
        fun buildPaths(node: String, path: List<String> = emptyList()): List<List<String>> {
            val newPath = path + node
            return nodesMap.getValue(node).flatMap { child ->
                when {
                    child == "end" -> listOf(newPath + child)
                    !child.isSmallCave() || child !in path -> buildPaths(child, newPath)
                    else -> emptyList()
                }
            }
        }

        buildPaths("start").size
    },
    testsPart1 = result(10),
    part2 = { input ->
        fun visitSmallCave(path: List<String>, child: String, canTwice: Boolean) = if (canTwice) {
            path.count { it == child } < 2
        } else {
            child !in path
        }

        fun nowCanTwice(path: List<String>, canTwice: Boolean) = if (canTwice) {
            val nodes = mutableSetOf<String>()
            path.forEach { node ->
                if (node.isSmallCave() && !nodes.add(node)) return false
            }
            true
        } else false

        val nodesMap = input.toNodesMap()
        fun buildPaths(node: String, path: List<String> = emptyList(), canTwice: Boolean = true): List<List<String>> {
            val newPath = path + node
            return nodesMap.getValue(node).flatMap { child ->
                when {
                    child == "start" -> emptyList()
                    child == "end" -> listOf(newPath + child)
                    !child.isSmallCave() || visitSmallCave(path, child, canTwice) -> {
                        buildPaths(child, newPath, nowCanTwice(path + child, canTwice))
                    }
                    else -> emptyList()
                }
            }
        }

        buildPaths("start").size
    },
    testsPart2 = result(36),
)

private fun List<List<String>>.toNodesMap() = flatMap { (v1, v2) -> listOf(v1 to v2, v2 to v1) }
    .distinct()
    .groupBy(keySelector = { it.first }, valueTransform = { it.second })

private fun String.isSmallCave() = all { it.isLowerCase() }
