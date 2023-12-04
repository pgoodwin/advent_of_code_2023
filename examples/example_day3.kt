import java.io.File


fun main() {
    val diagnostics = File("diagnostic_data.txt").readLines().map(String::toCharArray)
    val width = diagnostics[0].size
    val histogram = histogramFor(diagnostics)

    var gamma = 0
    var epsilon = 0
    histogram.forEachIndexed { i, entry ->
        gamma *= 2
        epsilon *= 2
        if (entry['1']!! > entry['0']!!) {
            gamma += 1
        } else {
            epsilon += 1
        }
    }

    println(gamma)
    println(epsilon)
    println(gamma * epsilon)

    var candidateOxygenReadings = diagnostics
    var candidateCo2Readings = diagnostics
    for (i in 0..width) {
        val oxyFrequentBitValues = mostFrequentAtBitPositions(candidateOxygenReadings)
        val co2FrequentBitValues = mostFrequentAtBitPositions(candidateCo2Readings)
        val oxygenReading = if (oxyFrequentBitValues[i] == '0') '0' else '1'
        val co2Reading = if (co2FrequentBitValues[i] == '0') '1' else '0'
        if (candidateOxygenReadings.size > 1) candidateOxygenReadings =
            candidateOxygenReadings.filter { reading -> reading[i] == oxygenReading }
        if (candidateCo2Readings.size > 1) candidateCo2Readings =
            candidateCo2Readings.filter { reading -> reading[i] == co2Reading }
        if (candidateOxygenReadings.size == 1 && candidateCo2Readings.size == 1)
            break
    }

    val oxygenReadingAsInt = readingToInt(candidateOxygenReadings.first())
    val co2ReadingAsInt = readingToInt(candidateCo2Readings.first())

    println(oxygenReadingAsInt)
    println(co2ReadingAsInt)
    println(oxygenReadingAsInt * co2ReadingAsInt)
}

private fun mostFrequentAtBitPositions(readings: List<CharArray>): CharArray {
    val width = readings[0].size
    val histogram = histogramFor(readings)
    val frequentValues = CharArray(width) { ' ' }
    histogram.forEachIndexed { i, entry ->
        if (entry['1']!! > entry['0']!!)
            frequentValues[i] = '1'
        else if (entry['1']!! < entry['0']!!)
            frequentValues[i] = '0'
    }
    return frequentValues
}

private fun histogramFor(readings: List<CharArray>): Array<MutableMap<Char, Int>> {
    val width = readings[0].size
    val histogram = Array<MutableMap<Char, Int>>(width) { mutableMapOf('1' to 0, '0' to 0) }
    readings.forEach { bits ->
        bits.forEachIndexed { i, bitVal ->
            histogram[i][bitVal] = histogram[i][bitVal]!! + 1
        }
    }
    return histogram
}

private fun readingToInt(reading: CharArray): Int {
    var readingAsInt = 0
    reading.forEach { bit ->
        readingAsInt *= 2
        if (bit == '1')
            readingAsInt++
    }
    return readingAsInt
}


