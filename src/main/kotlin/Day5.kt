import java.io.File

class Mapping() {
    private val mappings: MutableList<Entry> = mutableListOf()

    data class Entry(val fromStart: Long, val toStart: Long, val length: Long)

    fun add(fromStart: Long, toStart: Long, length: Long) {
        mappings.add(Entry(fromStart, toStart, length))
    }

    fun find(from: Long): Long {
        mappings.forEach{ entry ->
            val index = from - entry.fromStart
            if (index >= 0 && index < entry.length) {
                return entry.toStart + index
            }
        }
        return from
    }
}

fun Long.translate(mapping: Mapping): Long = mapping.find(this)

class Day5 (fileName: String) {
        private val soilLines = File(fileName).readLines().iterator()
        private val seeds = parseSeeds(soilLines.next())
        init{soilLines.next()}
        private val toSoil = parseMapping(soilLines)
        private val toFertilizer = parseMapping(soilLines)
        private val toWater = parseMapping(soilLines)
        private val toLight = parseMapping(soilLines)
        private val toTemperature = parseMapping(soilLines)
        private val toHumidity = parseMapping(soilLines)
        private val toLocation = parseMapping(soilLines)

    fun part1() {
        seeds.associateBy { seed ->
            seed.translate(toSoil)
                .translate(toFertilizer)
                .translate(toWater)
                .translate(toLight)
                .translate(toTemperature)
                .translate(toHumidity)
                .translate(toLocation).also(::println)
        }.minBy(Map.Entry<Long, Long>::value).also(::println)
    }
}

fun main() {
    val day5 = Day5("soil.txt")
    day5.part1()
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
            fromStart = params.elementAt(1).toLong(),
            toStart = params.elementAt(0).toLong(),
            length = params.elementAt(2).toLong(),
        )
        nextLine = if (lines.hasNext()) lines.next() else ""
    }
    return mapping
}