import java.io.File

fun main() {
    val soilLines = File("soil.txt").readLines().iterator()
    val seeds = parseSeeds(soilLines.next())


}

private fun parseSeeds(seedString: String) = "[^:]: (?<seed>.+)"
    .toRegex().find(seedString)!!
    .groups["seed"]?.value!!
    .splitOnSpaces()
    .map { it.toLong().also(::println) }