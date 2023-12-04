import java.io.File

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val energies = File("octopus_energy.txt").readLines()
    val startingEnergyAtPositionMut = mutableMapOf<Point, Int>()
    energies.forEachIndexed { y, line ->
        line.forEachIndexed { x, energy ->
            startingEnergyAtPositionMut[Point(x, y)] = energy.digitToInt()
        }
    }
    val startingEnergyAtPosition = startingEnergyAtPositionMut.toMap()
    (0..300)
        .fold(Pair(startingEnergyAtPosition, 0)) { (energyAtPosition, flashCount), iteration ->
            println("$iteration:")
            val (newEnergyAtPosition, newFlashes) = step(energyAtPosition)
            Pair(newEnergyAtPosition, (flashCount + newFlashes).also { println("$it\n") })
        }
}

fun printEnergy(energies: LevelMap) {
    val maxX = energies.keys.maxOf(Point::first)
    val maxY = energies.keys.maxOf(Point::second)
    print("+")
    print((0..maxX).map { "-" }.joinToString(""))
    println("+")
    (0..maxY).forEach { y ->
        print("|")
        print((0..maxX).map {
            val energy = energies[Pair(it, y)]!!
            when (energy) {
                0 -> " "
                1 -> "`"
                2 -> ","
                3 -> "-"
                4 -> "="
                5 -> "x"
                6 -> "#"
                7 -> "0"
                8 -> "X"
                9 -> "@"
                else -> energy.toString()
            }
        }.joinToString(""))
        println("|")
    }
    print("+")
    print((0..maxX).map { "-" }.joinToString(""))
    println("+")
}

fun step(energyAtPosition: LevelMap): Pair<LevelMap, Int> {
    val newEnergyAtPosition = energyAtPosition.mapValues { it.value + 1 }
    val (flashedEnergyAtPosition, flashCount) = propagateFlashes(newEnergyAtPosition, setOf())
    val finalEnergyAtPosition = flashedEnergyAtPosition.mapValues {
        if (it.value > 9) 0 else it.value
    }.also(::printEnergy)
    return Pair(finalEnergyAtPosition, flashCount)
}

fun propagateFlashes(
    energyAtPosition: LevelMap,
    flashedPositions: Set<Point>
): Pair<LevelMap, Int> {
    val newFlashes = energyAtPosition
        .filter { it.value > 9 }
        .filter { !flashedPositions.contains(it.key) }
        .map { it.key }
        .toSet()

    if (newFlashes.isEmpty()) return Pair(energyAtPosition, flashedPositions.size)

    val flashNeighbors = newFlashes.flatMap {
        listOf(
            it.copy(first = it.first + 1),
            it.copy(first = it.first - 1),
            it.copy(second = it.second + 1),
            it.copy(second = it.second - 1),
            Point(it.first + 1, it.second + 1),
            Point(it.first + 1, it.second - 1),
            Point(it.first - 1, it.second - 1),
            Point(it.first - 1, it.second + 1),
        ).filter { position -> energyAtPosition.containsKey(position) }
    }
    val newEnergyAtPosition1 =
        energyAtPosition.mapValues { energyPoint ->
            energyPoint.value + flashNeighbors.count { it == energyPoint.key }
        }
    return propagateFlashes(
        newEnergyAtPosition1,
        flashedPositions + newFlashes
    )
}
