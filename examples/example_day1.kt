import java.io.File

fun main() {
    val depths = File("depths.txt").readLines().map(String::toInt)
    val windows = (0..depths.size-3).map { depths[it] + depths[it+1] + depths[it+2] }

    val depthsResult = depths.fold(Pair<Int?, Int>(null, 0)) { acc, next ->
        Pair(
            next,
            if (acc.first != null && next > acc.first!!)
                acc.second + 1
            else
                acc.second
        )
    }
    val windowsResult = windows.fold(Pair<Int?, Int>(null, 0)) { acc, next ->
        Pair(
            next,
            if (acc.first != null && next > acc.first!!)
                acc.second + 1
            else
                acc.second
        )
    }
    println(depthsResult.second)
    println(windowsResult.second)
}