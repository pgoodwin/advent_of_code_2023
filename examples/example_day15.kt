import java.io.File
import kotlin.math.max

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val chitinLines = File("cave_chitins.txt").readLines()
    val chitinLevels = mutableMapOf<Point, Int>().withDefault { 0 }
    val maxBaseChitinX = chitinLines[0].length - 1
    val maxBaseChitinY = chitinLines.size - 1
    chitinLines.forEachIndexed { y, levels ->
        levels.forEachIndexed { x, levelAsChar ->
            chitinLevels[Point(x, y)] = levelAsChar.digitToInt()
        }
    }

    val expandedChitin = expandedChitin(maxBaseChitinX + 1, maxBaseChitinY + 1, chitinLevels)
    ChitinExplorer(maxBaseChitinX, maxBaseChitinY, expandedChitin).findBestPath()
    ChitinExplorer(((maxBaseChitinX + 1) * 5) - 1, ((maxBaseChitinY + 1) * 5) - 1, expandedChitin).findBestPath()
}

private fun expandedChitin(width: Int, depth: Int, chitinLevels: MutableMap<Point, Int>) =
    { location: Point ->
        val x = location.first % width
        val y = location.second % depth
        val enhancement = location.first / width + location.second / depth
        val enhancedChitin = (chitinLevels[Point(x, y)]!! + enhancement) % 9
        if (enhancedChitin == 0) 9 else enhancedChitin
    }


class ChitinExplorer(
    private val maxX: Int,
    private val maxY: Int,
    private val chitinAtLocation: (Point) -> Int
) {
    private val endPoint = Point(maxX, maxY)
    private val lowestArrivalCost = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }

    fun findBestPath() {
        findBestPathBreadthFirst(Point(0, 0))
    }

    private fun findBestPathBreadthFirst(startingPoint: Point) {
        val searchPoints = mutableListOf(startingPoint)
        lowestArrivalCost[startingPoint] = 0
        var maxSearchListSize = 0
        var iterations = 0
        while (searchPoints.size > 0) {
            searchPoints.sortBy { estimatedFinalPathCost(it) }
            searchPoints.addAll(findNeighborsWithLowArrivalCosts(searchPoints.removeFirst()))
            maxSearchListSize = max(maxSearchListSize, searchPoints.size)
            iterations++
            if (iterations % 10000 == 0)
                println("cur search points: ${searchPoints.size} iterations: $iterations arrival costs calculated: ${lowestArrivalCost.size}")
        }
    }

    private fun findNeighborsWithLowArrivalCosts(location: Pair<Int, Int>): Collection<Point> {
        return neighborsOf(location).filter { neighbor ->
            val newArrivalCost = lowestArrivalCost[location]!! + chitinAtLocation(neighbor)
            (lowestArrivalCost.getValue(neighbor) > newArrivalCost).also { updatedCost ->
                if (updatedCost) {
                    lowestArrivalCost[neighbor] = newArrivalCost
                    if (neighbor == endPoint) println(newArrivalCost)
                }
            }
        }
    }

    private fun estimatedFinalPathCost(location: Point): Int {
        return lowestArrivalCost.getValue(location)
    }

    private fun neighborsOf(currentLocation: Point): List<Point> {
        val neighbors = mutableListOf<Point>()
        if (currentLocation.first < maxX) neighbors.add(currentLocation.copy(first = currentLocation.first + 1))
        if (currentLocation.second < maxY) neighbors.add(currentLocation.copy(second = currentLocation.second + 1))
        if (currentLocation.first > 0
            && currentLocation.second > 0
            && currentLocation.second < maxY
        ) neighbors.add(currentLocation.copy(first = currentLocation.first - 1))
        if (currentLocation.second > 0
            && currentLocation.first > 0
            && currentLocation.first < maxX
        ) neighbors.add(currentLocation.copy(second = currentLocation.second - 1))
        return neighbors.toList()
    }
}
