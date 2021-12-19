import kotlin.math.roundToInt

// https://adventofcode.com/2021/day/18
object Day18 : AdventOfCode, Solution by Omar_Miatello(
    day = 18,
    parser = { lines ->
        fun CharIterator.parseSnailfishNumber(c: Char): SnailfishNumber = if (c.isDigit()) {
            SnailfishLiteral(
                value = buildString {
                    var char = c
                    while (char.isDigit()) {
                        append(char)
                        char = next()
                    }
                }.toInt()
            )
        } else {
            SnailfishPair(
                left = parseSnailfishNumber(next()),
                right = parseSnailfishNumber(next()),
            ).also { if (hasNext()) next() }
        }
        lines.map { line -> line.iterator().run { parseSnailfishNumber(next()) } }
    },
    part1 = { input ->
        input.reduce { acc, next -> SnailfishPair(acc.tryReduce(), next).tryReduce() }.tryReduce().points()
    },
    testsPart1 = results(
        "[[1,2],[[3,4],5]]".lines() to 143,
        "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]".lines() to 1384,
        "[[[[1,1],[2,2]],[3,3]],[4,4]]".lines() to 445,
        "[[[[3,0],[5,3]],[4,4]],[5,5]]".lines() to 791,
        "[[[[5,0],[7,4]],[5,5]],[6,6]]".lines() to 1137,
        "[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]".lines() to 1384,
        "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]".lines() to 3488,
        """[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]""".lines() to 4140,
    ),
    part2 = { input ->
        input
            .flatMap { left -> (input - left).map { right -> SnailfishPair(left, right) } }
            .maxOf { it.tryReduce().points() }
    },
    testsPart2 = result(3993),
)

private sealed interface SnailfishNumber {
    fun points(): Int = when (this) {
        is SnailfishLiteral -> value
        is SnailfishPair -> left.points() * 3 + right.points() * 2
    }

    fun tryReduce(): SnailfishNumber {
        var last = this
        var ended = false
        while (!ended) {
            var current = last.tryExplode()
            if (current == last) current = last.trySplit()
            (current == last).also {
                ended = it
                last = current
            }
        }
        return last
    }

    private fun tryExplode(): SnailfishNumber {
        var lastIndex = -1
        var explodedLeft = 0
        var explodedRight = 0
        var found = false
        fun SnailfishNumber.explodeStep1(loop: Int): SnailfishNumber = when (this) {
            is SnailfishLiteral -> {
                if (!found) lastIndex++
                this
            }
            is SnailfishPair -> {
                when {
                    !found && loop == 1 && left is SnailfishPair -> {
                        found = true
                        explodedLeft = (left.left as SnailfishLiteral).value
                        explodedRight = (left.right as SnailfishLiteral).value
                        SnailfishPair(
                            left = SnailfishLiteral(0),
                            right = right,
                        )
                    }
                    !found && loop == 1 && right is SnailfishPair -> {
                        found = true
                        explodedLeft = (right.left as SnailfishLiteral).value
                        explodedRight = (right.right as SnailfishLiteral).value
                        SnailfishPair(
                            left = left.also { lastIndex++ },
                            right = SnailfishLiteral(0),
                        )
                    }
                    else -> SnailfishPair(
                        left = left.explodeStep1(loop = loop - 1),
                        right = right.explodeStep1(loop = loop - 1),
                    )
                }
            }
        }

        fun SnailfishNumber.explodeStep2(): SnailfishNumber =
            when (this) {
                is SnailfishLiteral -> {
                    when (lastIndex--) {
                        0 -> SnailfishLiteral(value + explodedLeft)
                        -2 -> SnailfishLiteral(value + explodedRight)
                        else -> this
                    }
                }
                is SnailfishPair -> SnailfishPair(
                    left = left.explodeStep2(),
                    right = right.explodeStep2(),
                )
            }

        return explodeStep1(4).let { if (found) it.explodeStep2() else it }
    }

    private fun trySplit(): SnailfishNumber {
        var splitted = false
        fun SnailfishNumber.splitStep1(): SnailfishNumber = when (this) {
            is SnailfishLiteral -> if (!splitted && value > 9) SnailfishPair(
                left = SnailfishLiteral((value / 2f).toInt()),
                right = SnailfishLiteral((value / 2f).roundToInt()),
            ).also { splitted = true } else this
            is SnailfishPair -> SnailfishPair(
                left = left.splitStep1(),
                right = right.splitStep1(),
            )
        }
        return splitStep1()
    }
}

private data class SnailfishPair(val left: SnailfishNumber, val right: SnailfishNumber) : SnailfishNumber {
    override fun toString() = "[$left,$right]"
}

private data class SnailfishLiteral(val value: Int) : SnailfishNumber {
    override fun toString() = value.toString()
}