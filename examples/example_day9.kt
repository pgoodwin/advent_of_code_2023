import java.io.File

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val rawHeights = File("lavacave_heightmap.txt").readLines()
    val heightAtPosition = mutableMapOf<Pair<Int, Int>, Int>()
    rawHeights.forEachIndexed { y, rowOfHeights ->
        rowOfHeights.forEachIndexed { x, heightChar ->
            heightAtPosition[Pair(x, y)] = heightChar.digitToInt()
        }
    }

    val lowPoints = heightAtPosition.keys.filter { isLowPoint(it, heightAtPosition) }
    lowPoints.sumOf { (heightAtPosition[it]!! + 1) }.also(::println)

    lowPoints.map { buildBasin(it, heightAtPosition) }
        .sortedByDescending { it.size }
        .take(3)
        .map {it.size}
        .reduce { acc, value -> acc * value }
        .also(::println)
}

fun buildBasin(
    lowPoint: Pair<Int, Int>,
    heightAtPosition: MutableMap<Pair<Int, Int>, Int>,
    existingBasin: List<Pair<Int, Int>> = mutableListOf(lowPoint)
): List<Pair<Int, Int>> {
    val newBasinPoints =
        adjacentPoints(lowPoint).filter { (heightAtPosition[it] ?: 9) != 9 && !existingBasin.contains(it) }
    val biggerBasin = existingBasin + newBasinPoints
    return newBasinPoints.fold(biggerBasin) { basin, point -> buildBasin(point, heightAtPosition, basin)}
}

private fun adjacentPoints(lowPoint: Pair<Int, Int>) = listOf(
    lowPoint.copy(first = lowPoint.first + 1),
    lowPoint.copy(first = lowPoint.first - 1),
    lowPoint.copy(second = lowPoint.second + 1),
    lowPoint.copy(second = lowPoint.second - 1),
)

private fun min(ints: List<Int?>): Int {
    return ints.fold(Int.MAX_VALUE) { curMin, next ->
        val compVal = next ?: Int.MAX_VALUE
        if (compVal < curMin)
            compVal
        else
            curMin
    }
}

private fun isLowPoint(point: Pair<Int, Int>, heightAtPosition: Map<Pair<Int, Int>, Int>) =
    heightAtPosition[point]!! < min(adjacentPoints(point).map { (heightAtPosition[it] ?: Int.MAX_VALUE) })
