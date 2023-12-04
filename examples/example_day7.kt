import java.io.File
import java.lang.StrictMath.abs

fun main() {
    val crabPositions = File("crab_positions.txt").readText().split(",").map(String::toInt)

    val positions = crabPositions //arrayListOf<Int>(16, 1, 2, 0, 4, 2, 7, 1, 2, 14)
    val alignCandidates = (positions.minOrNull() ?: 0).rangeTo(positions.maxOrNull() ?: 0).map { alignPoint ->
        positions.sumOf { position -> abs(position - alignPoint) }
    }
    val shortest = alignCandidates.minOrNull() ?: 0
    println(shortest)
    println(alignCandidates.indexOf(shortest))

    val newAlignCandidates = (positions.minOrNull() ?: 0).rangeTo(positions.maxOrNull() ?: 0).map { alignPoint ->
        positions.sumOf { position -> fuelCost(abs(position - alignPoint)) }
    }
    val newShortest = newAlignCandidates.minOrNull() ?: 0
    println(newShortest)
    println(newAlignCandidates.indexOf(newShortest))
}

fun fuelCost(distance: Int) = (1 + distance) * distance / 2



