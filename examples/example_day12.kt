import java.io.File

fun main() {
    val tunnels = readTunnels("cave_map.txt")
    val caves = mapCaves(tunnels)
    traverse(caves, ::isLargeOrUnexplored, listOf("start"), "end", listOf()).size.also(::println)
    println("\n\n\n********************************************************************************************************\n\n\n")
    traverse(caves, ::isLargeOrThereIsExtraExploreTime, listOf("start"), "end", listOf()).size.also(::println)
}

private fun readTunnels(tunnelFile: String): List<List<String>> {
    val tunnelRegEx = "(\\w+)-(\\w+)".toRegex()
    val tunnels = File(tunnelFile).readLines().map {
        val (_, fromCave, toCave) = tunnelRegEx.find(it)!!.groupValues
        listOf(fromCave, toCave)
    }
    return tunnels
}

private fun mapCaves(tunnels: List<List<String>>): Map<String, List<String>> {
    println("Here are the caves and the paths leading between them:")
    val caveNames = tunnels.flatten().toSet()
    return caveNames.associate { cave ->
        cave to tunnels
            .filter { tunnel -> tunnel.contains(cave) }
            .map { it.first { otherCave -> cave != otherCave } }
    }.onEach(::println).also { println() }
}

fun traverse(
    caves: Map<String, List<String>>,
    explorableCriteria: (List<String>, String, String) -> Boolean,
    currentPath: List<String>,
    end: String,
    previousTraversals: List<List<String>>
): List<List<String>> {
    if (currentPath.last() == end) return previousTraversals.append(currentPath.also(::println))
    val explorableCaves = caves[currentPath.last()]!!.filter { connectedCave ->
        explorableCriteria(currentPath, connectedCave, end)
    }
    return explorableCaves.fold(previousTraversals) { currentTraversals, connectedCave ->
        traverse(caves, explorableCriteria, currentPath.append(connectedCave), end, currentTraversals)
    }
}

fun isLargeOrUnexplored(path: List<String>, caveName: String, ignored: String): Boolean {
    return !path.contains(caveName) || isLarge(caveName)
}

private fun isLargeOrThereIsExtraExploreTime(
    currentPath: List<String>,
    connectedCave: String,
    end: String
) = (isLarge(connectedCave)
        || !currentPath.contains(connectedCave)
        || (
        !containsDuplicateSmallCaves(currentPath)
                && connectedCave != currentPath.first()
                && connectedCave != end
        ))

fun containsDuplicateSmallCaves(path: List<String>): Boolean {
    val smallCavesOnPath = path.filter { !isLarge(it) }
    return smallCavesOnPath.size > smallCavesOnPath.toSet().size
}

fun isLarge(caveName: String): Boolean {
    return caveName.first().isUpperCase()
}

private fun <E> List<E>.append(newElement: E): List<E> {
    return toMutableList().apply { add(newElement) }.toList()
}

