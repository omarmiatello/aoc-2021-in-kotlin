// https://adventofcode.com/2021/day/22
object Day22 : AdventOfCode, Solution by Omar_Miatello(
    day = 22,
    parser = { lines ->
        val regex = "(on|off) x=(-?\\d+)\\.\\.(-?\\d+),y=(-?\\d+)\\.\\.(-?\\d+),z=(-?\\d+)\\.\\.(-?\\d+)".toRegex()
        lines.map { line ->
            val values = regex.find(line)!!.groupValues
            RebootStep(
                isOn = values[1] == "on",
                cuboid = Cuboid(
                    x = values[2].toInt()..values[3].toInt(),
                    y = values[4].toInt()..values[5].toInt(),
                    z = values[6].toInt()..values[7].toInt(),
                ),
            )
        }
    },
    part1 = { input ->
        input.reversed()
            .mapNotNull { it.intersect(Cuboid(-50..50, -50..50, -50..50)) }
            .fold(emptyList<RebootStep>()) { acc, rebootStep -> acc + rebootStep.remove(acc) }
            .filter { it.isOn }
            .sumOf { it.cuboid.size }
    },
    testsPart1 = results(
        """on x=10..12,y=10..12,z=10..12
on x=11..13,y=11..13,z=11..13
off x=9..11,y=9..11,z=9..11
on x=10..10,y=10..10,z=10..10""".lines() to 39,
        """on x=-20..26,y=-36..17,z=-47..7
on x=-20..33,y=-21..23,z=-26..28
on x=-22..28,y=-29..23,z=-38..16
on x=-46..7,y=-6..46,z=-50..-1
on x=-49..1,y=-3..46,z=-24..28
on x=2..47,y=-22..22,z=-23..27
on x=-27..23,y=-28..26,z=-21..29
on x=-39..5,y=-6..47,z=-3..44
on x=-30..21,y=-8..43,z=-13..34
on x=-22..26,y=-27..20,z=-29..19
off x=-48..-32,y=26..41,z=-47..-37
on x=-12..35,y=6..50,z=-50..-2
off x=-48..-32,y=-32..-16,z=-15..-5
on x=-18..26,y=-33..15,z=-7..46
off x=-40..-22,y=-38..-28,z=23..41
on x=-16..35,y=-41..10,z=-47..6
off x=-32..-23,y=11..30,z=-14..3
on x=-49..-5,y=-3..45,z=-29..18
off x=18..30,y=-20..-8,z=-3..13
on x=-41..9,y=-7..43,z=-33..15
on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
on x=967..23432,y=45373..81175,z=27513..53682""".lines() to 590784,
    ),
    part2 = { input ->
        input.reversed()
            .fold(emptyList<RebootStep>()) { acc, rebootStep -> acc + rebootStep.remove(acc) }
            .filter { it.isOn }
            .sumOf { it.cuboid.size }
    },
    testsPart2 = result(2758514936282235),
)

private data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange) {
    val size = x.size.toLong() * y.size * z.size

    fun intersect(other: Cuboid): Cuboid? {
        val x1 = x.intersect(other.x)
        val y1 = y.intersect(other.y)
        val z1 = z.intersect(other.z)
        return if (x1 != null && y1 != null && z1 != null) Cuboid(x1, y1, z1) else null
    }

    fun remove(other: Cuboid): List<Cuboid> =
        if ((other.x in x || x in other.x) && (other.y in y || y in other.y) && (other.z in z || z in other.z)) {
            buildList {
                if (x.first < other.x.first) add(Cuboid(x.first until other.x.first, y, z))
                if (other.x.last < x.last) add(Cuboid(other.x.last + 1..x.last, y, z))
                val xRange = other.x.first.coerceAtLeast(x.first)..other.x.last.coerceAtMost(x.last)
                if (y.first < other.y.first) add(Cuboid(xRange, y.first until other.y.first, z))
                if (other.y.last < y.last) add(Cuboid(xRange, other.y.last + 1..y.last, z))
                val yRange = other.y.first.coerceAtLeast(y.first)..other.y.last.coerceAtMost(y.last)
                if (z.first < other.z.first) add(Cuboid(xRange, yRange, z.first until other.z.first))
                if (other.z.last < z.last) add(Cuboid(xRange, yRange, other.z.last + 1..z.last))
            }
        } else listOf(this)

    fun remove(others: List<Cuboid>): List<Cuboid> =
        others.fold(listOf(this)) { acc, other -> acc.flatMap { it.remove(other) } }
}

private data class RebootStep(val isOn: Boolean, val cuboid: Cuboid) {

    fun intersect(other: Cuboid) = cuboid.intersect(other)?.let { RebootStep(isOn, it) }

    fun remove(others: List<RebootStep>): List<RebootStep> =
        cuboid.remove(others.map { it.cuboid }).map { RebootStep(isOn, it) }
}

private val IntRange.size get() = last - first + 1
private operator fun IntRange.contains(other: IntRange) = other.first in this || other.last in this
private fun IntRange.intersect(other: IntRange) =
    if (first in other || last in other) first.coerceIn(other)..last.coerceIn(other) else null