@file:OptIn(ExperimentalStdlibApi::class)

import java.io.File

fun main() {
    val hexChars = File("bits_message.txt").readText().toCharArray().dropLast(1)
    val binaryBits = HexCharToBinaryIterator(hexChars.iterator())

    // some test cases
    println(HexCharToBinaryIterator(hexChars.iterator()).asSequence().toList())
    printPacketsFrom("D2FE28")
    printPacketsFrom("38006F45291200")
    printPacketsFrom("EE00D40C823060")
    printPacketsFrom("C200B40A82").also {println(" expected 3: got: ${it.evaluate()}")}
    printPacketsFrom("04005AC33890").also {println(" expected 54: got: ${it.evaluate()}")}
    printPacketsFrom("880086C3E88112").also {println(" expected 7: got: ${it.evaluate()}")}
    printPacketsFrom("CE00C43D881120").also {println(" expected 9: got: ${it.evaluate()}")}
    printPacketsFrom("D8005AC2A8F0").also {println(" expected 1: got: ${it.evaluate()}")}
    printPacketsFrom("F600BC2D8F").also {println(" expected 0: got: ${it.evaluate()}")}
    printPacketsFrom("9C005AC2F8F0").also {println(" expected 0: got: ${it.evaluate()}")}
    printPacketsFrom("9C0141080250320F1802104A08").also {println(" expected 1: got: ${it.evaluate()}")}

    val topLevelPacket = readPacketFrom(binaryBits)
    println(topLevelPacket)
    println(topLevelPacket.accumulateVersionNumbers())
    println(topLevelPacket.evaluate())
}

private fun printPacketsFrom(packetAsString: String): Packet {
    println(HexCharToBinaryIterator(packetAsString.toCharArray().iterator()).asSequence().toList())
    val testBinaryBits = HexCharToBinaryIterator(packetAsString.toCharArray().iterator())
    return readPacketFrom(testBinaryBits).also(::println)
}

private class HexCharToBinaryIterator(private val eachHexChar: Iterator<Char>) :
    Iterator<Char> { // Assume we're representing boolean digits as chars for now
    var bits = listOf<Char>().iterator()

    override fun hasNext(): Boolean {
        return bits.hasNext() || eachHexChar.hasNext()
    }

    override fun next(): Char {
        if (!bits.hasNext())
            bits = eachHexChar.next().asHexBits().iterator()
        return bits.next()
    }
}

private data class Packet(val header: BITSHeader, val body: Any?) {
    fun accumulateVersionNumbers(): Int {
        return header.version + if (header.typeID != 4) {
            packetList().fold(0) { acc, packet ->
                acc + packet.accumulateVersionNumbers()
            }
        } else 0
    }

    fun evaluate(): Long {
        return when (header.typeID) {
            0 -> packetList().sumOf { it.evaluate() } // sum
            1 -> packetList().fold(1L) { acc, packet -> acc * packet.evaluate() } // product
            2 -> packetList().minOf { it.evaluate() } // minimum
            3 -> packetList().maxOf { it.evaluate() } // maximum
            4 -> numericValue().toLong() // value
            5 -> {
                val packets = packetList()
                if (packets.first().evaluate() > packets.last().evaluate()) 1L else 0L
            } // greater
            6 -> {
                val packets = packetList()
                if (packets.first().evaluate() < packets.last().evaluate()) 1L else 0L
            } // lesser
            7 -> {
                val packets = packetList()
                if (packets.first().evaluate() == packets.last().evaluate()) 1 else 0
            } // equal
            else -> 0
        }
    }

    private fun numericValue(): Long {
        return body as Long
    }

    private fun packetList(): List<Packet> {
        return body as List<Packet>
    }
}


private data class BITSHeader(val version: Int, val typeID: Int)

private fun readPacketFrom(binaryBits: Iterator<Char>): Packet {
    val header = readHeaderFrom(binaryBits)
    val body: Any = when (header.typeID) {
        4 -> readNumberLiteralFrom(binaryBits)
        else -> readSubPacketsFrom(binaryBits)
    }
    return Packet(header, body)
}

private fun readSubPacketsFrom(binaryBits: Iterator<Char>): List<Packet> {
    val lengthTypeIndicator = binaryBits.next()
    return if (lengthTypeIndicator == '0')
        readPacketsByTotalLength(binaryBits, binaryBits.nextCountAsInt(15))
    else
        readPacketsByCount(binaryBits, binaryBits.nextCountAsInt(11))
}

private fun readPacketsByCount(binaryBits: Iterator<Char>, packetCount: Int): List<Packet> {
    val packets = mutableListOf<Packet>()
    (1..packetCount).forEach {
        packets.add(readPacketFrom(binaryBits))
    }
    return packets.toList()
}

private fun readPacketsByTotalLength(binaryBits: Iterator<Char>, totalLength: Int): List<Packet> {
    val packets = mutableListOf<Packet>()
    val packetBits = binaryBits.nextCount(totalLength).iterator()
    while (packetBits.hasNext()) {
        packets.add(readPacketFrom(packetBits))
    }
    return packets.toList()
}

private fun readHeaderFrom(binaryBits: Iterator<Char>): BITSHeader {
    return BITSHeader(
        binaryBits.nextCountAsInt(3),
        binaryBits.nextCountAsInt(3),
    )
}

private fun readNumberLiteralFrom(binaryBits: Iterator<Char>): Long {
    var keepGoing: Boolean
    var value = 0L
    do {
        keepGoing = binaryBits.next() == '1'
        value = value shl 4
        value += binaryBits.nextCountAsInt(4)
    } while (keepGoing)
    return value
}

private fun <T> Iterator<T>.nextCount(count: Int): List<T> {
    return (1..count).map { _ -> this.next() }
}

private fun Iterator<Char>.nextCountAsInt(count: Int): Int {
    return nextCount(count).fold(0) { acc, value ->
        (acc shl 1) + if (value == '0') 0 else 1
    }
}

private fun Char.asHexBits() = this.digitToInt(16).asBits()

private fun Int.asBits() = listOf(
    if (this and 8 == 0) '0' else '1',
    if (this and 4 == 0) '0' else '1',
    if (this and 2 == 0) '0' else '1',
    if (this and 1 == 0) '0' else '1',
)
