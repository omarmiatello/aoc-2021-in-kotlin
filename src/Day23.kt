import kotlin.math.absoluteValue

// https://adventofcode.com/2021/day/23
object Day23 : AdventOfCode, Solution by Omar_Miatello(
    day = 23,
    parser = { lines ->
        Location(
            rooms = (0..3).map {
                Room(
                    state = "${lines[2][3 + it * 2]}${lines[3][3 + it * 2]}",
                    expectedAmphipod = energyMap.toList()[it].first,
                )
            }
        )
    },
    part1 = { input ->
        input.leastEnergyRequired(100)
    },
    testsPart1 = result(12521),
    part2 = { input ->
        val added = listOf("DD", "CB", "BA", "AC")
        input.copy(
            rooms = input.rooms
                .mapIndexed { i, room -> room.copy(state = "${room.state[0]}${added[i]}${room.state[1]}") },
            hallway = input.hallway.copy(maxConcurrent = 7),
        ).leastEnergyRequired(100)
    },
    testsPart2 = result(44169),
    //testsPart2 = results(), // 48057
)

private fun Location.leastEnergyRequired(maxBeforeStop: Int): Int {
    val potentialResults = mutableListOf<Location>()
    val locations = sortedSetOf(this)
    while (potentialResults.size < maxBeforeStop) {
        val first = locations.first()
        locations.remove(first)
        locations.addAll(first.allMoves())
        locations.firstOrNull { it.isCompleted() }?.also { potentialResults.add(it) }
    }
    return potentialResults.minOf { it.energyConsumed }
}

private val energyMap = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
private fun Char.energyTo(steps: Int) = energyMap.getValue(this) * steps
private fun Char.toRoomId() = energyMap.toList().indexOfFirst { this == it.first }

private data class Hallway(val state: String = "..0.1.2.3..", val maxConcurrent: Int = 3) {
    fun canMoveIn() = state.count { it.isLetter() } < maxConcurrent
    fun roomPosition(room: Int) = 2 + room * 2

    fun energyFor(p1: Int, p2: Int, c: Char = state[p1], fromRoom: Boolean): Int? {
        val start = kotlin.math.min(p1, p2)
        val end = kotlin.math.max(p1, p2)
        return if (c.isLetter() && state.substring(start..end).count { it.isLetter() } == if (fromRoom) 0 else 1) {
            c.energyTo(end - start)
        } else null
    }

    override fun toString() = state
}

private data class Room(
    val state: String,
    private val expectedAmphipod: Char,
) {
    private val validGuests = listOf('.', expectedAmphipod)
    fun completedPercent() = state.takeLastWhile { it == expectedAmphipod }.count().toDouble() / state.count()
    fun canMoveOut() = state.any { it !in validGuests }
    fun canMoveIn() = state.all { it in validGuests }
}

private data class Location(
    val rooms: List<Room>,
    val hallway: Hallway = Hallway(),
    val energyConsumed: Int = 0,
) : Comparable<Location> {
    fun isCompleted() = rooms.all { it.completedPercent() == 1.0 }
    fun allMoves(): List<Location> = (allMovesToRooms() + allMovesToHallway()).sorted()
    fun allMovesToHallway() = rooms.indices.flatMap { allMovesToHallwayFromRoom(it) }
    fun allMovesToHallwayFromRoom(roomId: Int) = hallway.state.indices.mapNotNull { moveRoomToHallway(roomId, it) }
    fun allMovesToRooms() = hallway.state.indices.mapNotNull { moveHallwayToRoom(it) }

    fun moveRoomToHallway(
        roomId: Int,
        end: Int,
    ): Location? {
        val room = rooms[roomId]
        return if (room.canMoveOut() && hallway.canMoveIn()) {
            val (iRoom, c) = room.state.withIndex().first { it.value.isLetter() }
            val roomEnergy = c.energyTo(iRoom + 1)
            val start = hallway.roomPosition(roomId)
            if (hallway.state[end] == '.') {
                val hallwayEnergy = hallway.energyFor(start, end, c, fromRoom = true)
                if (hallwayEnergy != null) {
                    Location(
                        rooms = rooms.mapIndexed { i, oldRoom ->
                            if (i == roomId) {
                                room.copy(state = room.state.replaceRange(iRoom..iRoom, "."))
                            } else oldRoom
                        },
                        hallway = hallway.copy(state = hallway.state.replaceRange(end..end, "$c")),
                        energyConsumed = energyConsumed + roomEnergy + hallwayEnergy,
                    )
                } else null
            } else null
        } else null
    }

    fun moveHallwayToRoom(start: Int): Location? {
        val c = hallway.state[start]
        return if (c.isLetter()) {
            val end = hallway.roomPosition(c.toRoomId())
            val hallwayEnergy = hallway.energyFor(start, end, c, fromRoom = false)
            if (hallwayEnergy != null) {
                val roomId = c.toRoomId()
                if (rooms[roomId].canMoveIn()) {
                    val room = rooms[roomId]
                    val iRoom = room.state.lastIndexOf('.')
                    val roomEnergy = c.energyTo(iRoom + 1)
                    Location(
                        rooms = rooms.mapIndexed { i, oldRoom ->
                            if (i == roomId) {
                                room.copy(state = room.state.replaceRange(iRoom..iRoom, "$c"))
                            } else oldRoom
                        },
                        hallway = hallway.copy(state = hallway.state.replaceRange(start..start, ".")),
                        energyConsumed = energyConsumed + roomEnergy + hallwayEnergy,
                    )
                } else null
            } else null
        } else null
    }

    override fun compareTo(other: Location) = compareValuesBy(this, other) {
        fun Char.energyToExit(start: Int) = energyTo(start + 1)

        fun Char.energyToHallway(start: Int, newRoomId: Int, fromRoom: Boolean) =
            it.hallway.energyFor(start, it.hallway.roomPosition(newRoomId), this, fromRoom)

        fun Char.energyToEnter(newRoomId: Int): Int {
            val newRoom = it.rooms[newRoomId]
            val enter = newRoom.state.indexOfLast { it == '.' } + 1
            return energyTo(if (newRoom.canMoveIn()) {
                enter
            } else {
                enter + newRoom.state.withIndex()
                    .sumOf { (i, it) -> if (it.isLetter()) it.energyTo(i + 2) else 0 }
            })
        }

        val energyForRooms = it.rooms.withIndex().sumOf { (roomId, room) ->
            room.state.withIndex().sumOf { (cId, c) ->
                val newRoomId = c.toRoomId()
                if (c.isLetter() && roomId != newRoomId) {
                    val energyToHallway = c.energyToHallway(it.hallway.roomPosition(roomId), newRoomId, fromRoom = true) ?: return@sumOf Int.MAX_VALUE
                    c.energyToExit(cId) + energyToHallway + c.energyToEnter(newRoomId)
                } else 0
            }
        }
        val energyForHallways = it.hallway.state.withIndex().sumOf { (start, c) ->
            if (c.isLetter()) {
                val energyToHallway = c.energyToHallway(start, c.toRoomId(), fromRoom = false) ?: return@sumOf Int.MAX_VALUE
                energyToHallway + c.energyToEnter(c.toRoomId())
            } else 0
        }
        energyForRooms + energyForHallways
    }

    override fun toString() = "$hallway (energy: $energyConsumed)\n" +
            rooms.first().state.indices.joinToString("\n") { n ->
                "  ${rooms.joinToString(" ") { it.state[n].toString() }}"
            }
}