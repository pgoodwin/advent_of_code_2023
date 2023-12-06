import java.io.File

class Mapping() {
    private val mappings: MutableList<Entry> = mutableListOf()

    data class Entry(val fromStart: Long, val toStart: Long, val length: Long)

    fun add(fromStart: Long, toStart: Long, length: Long) {
        mappings.add(Entry(fromStart, toStart, length))
    }
}

fun main() {
    val soilLines = File("soil.txt").readLines().iterator()
    val seeds = parseSeeds(soilLines.next())
    soilLines.next()
    val toSoil = parseMapping(soilLines)
    val toFertilizer = parseMapping(soilLines)
    val toWater = parseMapping(soilLines)
    val toLight = parseMapping(soilLines)
    val toTemperature = parseMapping(soilLines)
    val toHumidity = parseMapping(soilLines)
    val toLocation = parseMapping(soilLines)
}

private fun parseSeeds(seedString: String) = "[^:]: (?<seed>.+)"
    .toRegex().find(seedString)!!
    .groups["seed"]?.value!!
    .splitOnSpaces()
    .map(String::toLong)


private fun parseMapping(lines: Iterator<String>): Mapping {
    lines.next()
    val mapping = Mapping()
    var nextLine = lines.next()
    while (nextLine.isNotEmpty()) {
        val params = nextLine.splitOnSpaces()
        mapping.add(
            fromStart = params.elementAt(0).toLong(),
            toStart = params.elementAt(1).toLong(),
            length = params.elementAt(2).toLong(),
        )
        nextLine = if (lines.hasNext()) lines.next() else ""
    }
    return mapping
}