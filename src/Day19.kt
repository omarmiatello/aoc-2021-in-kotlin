import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

// https://adventofcode.com/2021/day/19
object Day19 : AdventOfCode, Solution by Omar_Miatello(
    day = 19,
    parser = { lines ->
        fun Iterator<String>.nextScanner() = Scanner(beacons = buildList {
            var completed = false
            while (!completed && hasNext()) {
                val line = next()
                if (line.isEmpty()) {
                    next()
                    completed = true
                } else {
                    add(Beacon(Coordinate(line.split(",").map { it.toInt() })))
                }
            }
        })
        buildList {
            val iterator = lines.drop(1).iterator()
            while (iterator.hasNext()) add(iterator.nextScanner())
        }
    },
    part1 = { input ->
        input.findAllBeacons().allBeacons.size
    },
    testsPart1 = result(79),
    part2 = { input ->
        val scanners = input.findAllBeacons().scannerPositions.map { it.value.coordinate }
        scanners.maxOf { s1 ->
            scanners.maxOf { s2 -> s1.distance(s2).asManhattanDistance() }
        }
    },
    testsPart2 = result(3621),
)

private fun List<Scanner>.findAllBeacons(): FullMap {
    val missingScanners = toMutableSet()
    val knownNearby = mutableSetOf<BeaconNearby>()
    return FullMap(
        scannerPositions = buildMap {
            var hasFound = false
            fun foundScannerPosition(scanner: Scanner, position: ScannerPosition) {
                hasFound = true
                put(scanner, position)
                knownNearby.addAll(scanner.nearbyList.map { it.move(position) })
                missingScanners.remove(scanner)
            }
            foundScannerPosition(missingScanners.first(), ScannerPosition(Coordinate(0, 0, 0), 0))
            while (hasFound && missingScanners.isNotEmpty()) {
                hasFound = false
                missingScanners.toList().forEach { scanner ->
                    scanner.nearbyList
                        .flatMap { nearby2 ->
                            val allVariant by lazy { nearby2.distance.allVariant }
                            knownNearby
                                .filter { nearby1 -> nearby1.euclideanDistance == nearby2.euclideanDistance }
                                .mapNotNull { nearby1 ->
                                    allVariant.indexOf(nearby1.distance).takeIf { it != -1 }?.let { variant ->
                                        ScannerPosition(
                                            coordinate = nearby2.b1.coordinate.variant(variant)
                                                .distance(nearby1.b1.coordinate),
                                            variant = variant
                                        )
                                    }
                                }
                        }
                        .groupingBy { it }
                        .eachCount().plus(ScannerPosition(Coordinate(0, 0, 0), 0) to 0)
                        .maxByOrNull { it.value }
                        ?.also { (scannerPosition, count) ->
                            if (count >= 12) {
                                foundScannerPosition(scanner, scannerPosition)
                            }
                        }
                }
            }
            if (missingScanners.isNotEmpty()) error("Cannot find position of $missingScanners")
        },
        allBeacons = knownNearby.map { it.b1 },
    )
}

private data class FullMap(val scannerPositions: Map<Scanner, ScannerPosition>, val allBeacons: List<Beacon>)

private data class Scanner(val beacons: List<Beacon>) {
    val nearbyList: List<BeaconNearby> = beacons.map { b1 ->
        (beacons - b1)
            .map { b2 ->
                val distance = b1.distance(b2)
                BeaconNearby(b1, b2, distance, distance.asEuclideanDistance())
            }
            .minByOrNull { it.euclideanDistance }!!
    }
}

@JvmInline
private value class Coordinate(val value: List<Int>) {
    constructor(x: Int, y: Int, z: Int) : this(listOf(x, y, z))

    val x get() = value[0]
    val y get() = value[1]
    val z get() = value[2]

    fun asEuclideanDistance() = euclideanDistance(*value.toTypedArray())
    fun asManhattanDistance() = abs(x) + abs(y) + abs(z)

    fun variant(idx: Int): Coordinate {
        val (rx, ry, rz, fx, fy, fz) = rotation[idx]
        return Coordinate(x = value[rx] * fx, y = value[ry] * fy, z = value[rz] * fz)
    }

    val allVariant get() = (0..23).map { variant(it) }

    fun distance(other: Coordinate) = Coordinate(x = other.x - x, y = other.y - y, z = other.z - z)

    operator fun plus(other: Coordinate) = Coordinate(x = x + other.x, y = y + other.y, z = z + other.z)

    override fun toString() = "[x:$x y:$y z:$z]"

    companion object {
        val rotation = listOf(
            listOf(0, 1, 2, 1, 1, 1),
            listOf(0, 1, 2, -1, -1, 1),
            listOf(0, 1, 2, 1, -1, -1),
            listOf(0, 1, 2, -1, 1, -1),
            listOf(0, 2, 1, -1, 1, 1),
            listOf(0, 2, 1, 1, -1, 1),
            listOf(0, 2, 1, 1, 1, -1),
            listOf(0, 2, 1, -1, -1, -1),
            listOf(1, 0, 2, -1, 1, 1),
            listOf(1, 0, 2, 1, -1, 1),
            listOf(1, 0, 2, 1, 1, -1),
            listOf(1, 0, 2, -1, -1, -1),
            listOf(1, 2, 0, 1, 1, 1),
            listOf(1, 2, 0, -1, -1, 1),
            listOf(1, 2, 0, 1, -1, -1),
            listOf(1, 2, 0, -1, 1, -1),
            listOf(2, 0, 1, 1, 1, 1),
            listOf(2, 0, 1, -1, -1, 1),
            listOf(2, 0, 1, 1, -1, -1),
            listOf(2, 0, 1, -1, 1, -1),
            listOf(2, 1, 0, -1, 1, 1),
            listOf(2, 1, 0, 1, -1, 1),
            listOf(2, 1, 0, 1, 1, -1),
            listOf(2, 1, 0, -1, -1, -1),
        )
    }
}

private data class ScannerPosition(val coordinate: Coordinate, val variant: Int)
private data class BeaconNearby(
    val b1: Beacon,
    val b2: Beacon,
    val distance: Coordinate,
    val euclideanDistance: Double
) {
    fun move(position: ScannerPosition) =
        BeaconNearby(b1.move(position), b2.move(position), distance.variant(position.variant), euclideanDistance)
}

private data class Beacon(val coordinate: Coordinate) {
    fun distance(other: Beacon) = coordinate.distance(other.coordinate)
    fun move(position: ScannerPosition) = Beacon(coordinate.variant(position.variant) + position.coordinate)
    override fun toString() = coordinate.toString()
}

private fun euclideanDistance(vararg points: Number) = sqrt(points.sumOf { it.toDouble().pow(2) })
private operator fun List<Int>.component6(): Int = get(5)