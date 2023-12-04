import java.io.File
import kotlin.math.abs

fun main() {
    val ventLineRegEx = "(\\d+),(\\d+) -> (\\d+),(\\d+)".toRegex()
    val ventLines = File("vent_lines.txt").readLines().map {
        val (_, x1, y1, x2, y2) = ventLineRegEx.find(it)!!.groupValues
        listOf(Pair(x1.toInt(), y1.toInt()), Pair(x2.toInt(), y2.toInt()))
    }
    val horizontalLines = ventLines.filter { it[0].first == it[1].first }
    val verticalLines = ventLines.filter { it[0].second == it[1].second }
    val histogram = mutableMapOf<Pair<Int, Int>, Int>()
    horizontalLines.forEach {
        for (i in range(it[0].second, it[1].second)) {
            increment(histogram, Pair(it[0].first, i))
        }
    }
    verticalLines.forEach {
        for (i in range(it[0].first, it[1].first)) {
            increment(histogram, Pair(i, it[0].second))
        }
    }
    println(histogram.filter { it.value > 1 }.size)

    val diagonalLines = ventLines.filter { abs(it[0].first - it[1].first) == abs(it[0].second - it[1].second) }
    diagonalLines.forEach {
        range(it[0].first, it[1].first).zip(
            range(it[0].second, it[1].second)
        ).forEach { increment(histogram, it) }
    }
    println(histogram.filter { it.value > 1 }.size)
}

private fun range(start: Int, end: Int): IntProgression {
    return if (start < end) start.rangeTo(end) else start.downTo(end)
}

fun increment(histogram: MutableMap<Pair<Int, Int>, Int>, point: Pair<Int, Int>) {
    val curVal = histogram[point] ?: 0
    histogram[point] = curVal + 1
}
