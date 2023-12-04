import java.io.File

class PatternAndNumberMap(patterns: List<String>) {
    private val numberForPattern = mutableMapOf<Set<Char>, Int>()
    private val patternForNumber = mutableMapOf<Int, Set<Char>>()

    private fun addRelation(number: Int, pattern: Set<Char>) {
        patternForNumber[number] = pattern
        numberForPattern[pattern] = number
    }

    init {
        val patternSets =
            patterns.map { val pattern = mutableSetOf<Char>(); pattern.addAll(it.asSequence()); pattern.toSet() }
        addRelation(1, patternSets.first { it.size == 2 })
        addRelation(4, patternSets.first { it.size == 4 })
        addRelation(7, patternSets.first { it.size == 3 })
        addRelation(8, patternSets.first { it.size == 7 })
        addRelation(3, patternSets.first { it.size == 5 && it.containsAll(patternForNumber[1]!!) })
        addRelation(9, patternForNumber[4]!! + patternForNumber[3]!!)
        val lowerLeftLeg = (patternForNumber[8]!! - patternForNumber[9]!!).first()
        addRelation(2, patternSets.first { it.size == 5 && it != patternForNumber[3]!! && it.contains(lowerLeftLeg) })
        addRelation(5, patternSets.first { it.size == 5 && it != patternForNumber[3]!! && it != patternForNumber[2]!! })
        addRelation(6, patternForNumber[5]!! + lowerLeftLeg)
        addRelation(0, patternSets.first { it.size == 6 && it != patternForNumber[6]!! && it != patternForNumber[9] })
    }

    fun translateReading(readingAsStrings: List<String>): Int {
        val readings = readingAsStrings.map {
            val readingString = mutableSetOf<Char>(); readingString.addAll(it.asSequence()); readingString.toSet()
        }
        return readings.fold(0) { value, digitPattern ->
            value * 10 + (numberForPattern[digitPattern] ?: 0)
        }
    }

    override fun toString(): String {
        return "PatternAndNumberMap(${patternForNumber.toSortedMap().toString()})"
    }
}

data class DisplayReading(val patterns: List<String>, val reading: List<String>)

fun main() {
    val displayReadingRegEx =
        """(\w+) (\w+) (\w+) (\w+) (\w+) (\w+) (\w+) (\w+) (\w+) (\w+) \| (\w+) (\w+) (\w+) (\w+)""".toRegex()
    val oneSegmentCount = 2
    val fourSegmentCount = 4
    val sevenSegmentCount = 3
    val eightSegmentCount = 7
    val knownSegmentCounts = listOf(oneSegmentCount, fourSegmentCount, sevenSegmentCount, eightSegmentCount)

    val displayObservations = File("segment_wiring.txt").readLines().map {
        val values = displayReadingRegEx.find(it)!!.groupValues
        DisplayReading(values.drop(1).take(10), values.drop(11).take(4))
    }

    println(displayObservations.flatMap { display ->
        display.reading.filter {
            knownSegmentCounts.contains(it.length)
        }
    }.size)

    println(displayObservations.sumBy { observation ->
        val translator = PatternAndNumberMap(observation.patterns)
        translator.translateReading(observation.reading).also(::println)
    })
}



