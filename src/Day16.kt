// https://adventofcode.com/2021/day/16
object Day16 : AdventOfCode, Solution by Omar_Miatello(
    day = 16,
    parser = { lines -> lines.first().flatMap { it.digitToInt(16).toString(2).padStart(4, '0').toList() } },
    part1 = { input ->
        fun Packet.versions(): Int = when (this) {
            is LiteralValue -> version
            is Operator -> version + packets.sumOf { it.versions() }
        }
        input.iterator().parsePacket().versions()
    },
    testsPart1 = results(
        "D2FE28".lines() to 6,
        "38006F45291200".lines() to 9,
        "EE00D40C823060".lines() to 14,
        "8A004A801A8002F478".lines() to 16,
        "620080001611562C8802118E34".lines() to 12,
        "C0015000016115A2E0802F182340".lines() to 23,
        "A0016C880162017C3686B18A3D4780".lines() to 31,
    ),
    part2 = { input ->
        fun Packet.resolve(): Long {
            return when (this) {
                is LiteralValue -> literalValue
                is Operator -> {
                    val values = packets.map { it.resolve() }
                    return when (type) {
                        OperatorType.Sum -> values.sum()
                        OperatorType.Product -> values.reduce { acc, n -> acc * n }
                        OperatorType.Minimum -> values.minOf { it }
                        OperatorType.Maximum -> values.maxOf { it }
                        OperatorType.LiteralValue -> error("What a Terrible Failure!")
                        OperatorType.GreaterThan -> if (values[0] > values[1]) 1 else 0
                        OperatorType.LessThan -> if (values[0] < values[1]) 1 else 0
                        OperatorType.EqualTo -> if (values[0] == values[1]) 1 else 0
                    }
                }
            }
        }
        input.iterator().parsePacket().resolve()
    },
    testsPart2 = results(
        "C200B40A82".lines() to 3,
        "04005AC33890".lines() to 54,
        "880086C3E88112".lines() to 7,
        "CE00C43D881120".lines() to 9,
        "D8005AC2A8F0".lines() to 1,
        "F600BC2D8F".lines() to 0,
        "9C005AC2F8F0".lines() to 0,
        "9C0141080250320F1802104A08".lines() to 1,
    ),
)

private sealed interface Packet
private data class LiteralValue(val version: Int, val type: OperatorType, val literalValue: Long) : Packet
private data class Operator(val version: Int, val type: OperatorType, val packets: List<Packet>) : Packet
private enum class OperatorType { Sum, Product, Minimum, Maximum, LiteralValue, GreaterThan, LessThan, EqualTo }

private fun Iterator<Char>.parsePacket(): Packet {
    val version = nextInt(size = 3)
    val type = OperatorType.values()[nextInt(size = 3)]
    return if (type == OperatorType.LiteralValue) LiteralValue(
        version = version,
        type = type,
        literalValue = nextLiteralValue(),
    ) else {
        val isTotalLength = next() == '0'
        Operator(
            version = version,
            type = type,
            packets = if (isTotalLength) {
                val totalLength = nextInt(15)
                val iterator = next(totalLength).iterator()
                buildList { while (iterator.hasNext()) add(iterator.parsePacket()) }
            } else {
                val numberOfPackets = nextInt(11)
                (1..numberOfPackets).map { parsePacket() }
            },
        )
    }
}

private fun Iterator<Char>.next(size: Int) = List(size) { next() }.joinToString("")
private fun Iterator<Char>.nextInt(size: Int) = next(size).toInt(2)
private fun Iterator<Char>.nextLiteralValue(): Long = buildString {
    while (next() == '1') append(next(size = 4))
    append(next(size = 4))
}.toLong(2)