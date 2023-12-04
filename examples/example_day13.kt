import java.io.File

fun main() {
    val lines = File("transparent_paper_instructions.txt").readLines().iterator()
    val paperLines = generateSequence {
        val nextLine = lines.next()
        if (nextLine.isEmpty()) null else nextLine
    }.toList()
    val instructionLines = generateSequence { if (lines.hasNext()) lines.next() else null }.toList()

    val paperPoints = paperLines.map { it.split(",").map(String::toInt).zipWithNext().single() }.toSet()
    val instructionRegEx = "fold along (\\w)=(\\d+)".toRegex()
    val instructions = instructionLines.map {
        val (_, axis, magnitude) = instructionRegEx.find(it)!!.groupValues
        Pair(axis, magnitude.toInt())
    }

    val newPaperPoints = instructions.fold(paperPoints) { paper, instruction -> foldPaper(paper, instruction) }
    printPaper(newPaperPoints)
}

fun foldPaper(paperPoints: Set<Pair<Int, Int>>, instruction: Pair<String, Int>): Set<Pair<Int, Int>> {
    val magnitude = instruction.second
    val axis = instruction.first
    return paperPoints.map {
        when (axis) {
            "x" -> if (magnitude > it.first) it else it.copy(first = magnitude * 2 - it.first)
            "y" -> if (magnitude > it.second) it else it.copy(second = magnitude * 2 - it.second)
            else -> it
        }
    }.toSet()
}

fun printPaper(marks: Set<Pair<Int, Int>>) {
    val maxX = marks.maxOf(Point::first)
    val maxY = marks.maxOf(Point::second)
    print("+")
    print((0..maxX).map { "-" }.joinToString(""))
    println("+")
    (0..maxY).forEach { y ->
        print("|")
        print((0..maxX).map { x ->
            val mark = marks.contains(Pair(x,y))
            if (mark)
                "#"
            else
                " "
        }.joinToString(""))
        println("|")
    }
    print("+")
    print((0..maxX).map { "-" }.joinToString(""))
    println("+")
}

