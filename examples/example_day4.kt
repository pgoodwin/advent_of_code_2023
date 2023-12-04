import java.io.File

class Board(val positionValues: List<List<Int>>) {
    fun isWinner(pickedNumbers: List<Int>): Boolean {
        (0..4).forEach { if (pickedNumbers.containsAll(positionValues[it])) return true }
        (0..4).forEach { v -> if (pickedNumbers.containsAll(positionValues.map { it[v] })) return true }
        return false
    }

    fun score(pickedNumbers: List<Int>): Int {
        return positionValues.flatten().filter { !pickedNumbers.contains(it) }.sum()
    }

    override fun toString(): String {
        val rows = StringBuilder()
        (0..4).forEach {
            rows.append(positionValues[it].fold("") { acc, num -> "$acc $num" })
            rows.append("\n")
        }
        return rows.toString()
    }

}

fun main() {
    val (bingoBalls, boards) = readBingoData()

    printFirstWinnerReport(bingoBalls, boards)
    printLastWinnerReport(bingoBalls, boards)
}

fun readBingoData(): Pair<List<Int>, List<Board>> {
    val bingoData = File("bingo.txt").readLines().iterator()
    val bingoBalls = bingoData.next().split(",").map(String::toInt)

    val boardRegEx = "\\w*(\\d+)".toRegex()
    val boards = mutableListOf<Board>()
    while (bingoData.hasNext()) {
        bingoData.next() // skip blank line
        boards.add(
            Board(
                (0..4).map {
                    boardRegEx.findAll(bingoData.next()).toList().map { it.value.toInt() }
                }
            )
        )
    }
    return Pair(bingoBalls, boards)
}

fun printLastWinnerReport(bingoBalls: List<Int>, boards: List<Board>) {
    val remainingBoards = mutableListOf<Board>()
    remainingBoards.addAll(boards)
    val pickedNumbers = mutableListOf<Int>()
    bingoBalls.forEach { picked ->
        pickedNumbers.add(picked)
        boards.forEach { board ->
            if (board.isWinner(pickedNumbers)) {
                remainingBoards.remove(board)
                if (remainingBoards.size == 0) {
                    println(board)
                    println(pickedNumbers)
                    println(board.score(pickedNumbers))
                    println(pickedNumbers.last())
                    println(board.score(pickedNumbers) * pickedNumbers.last())
                    return
                }
            }
        }
    }
}

private fun printFirstWinnerReport(bingoBalls: List<Int>, boards: List<Board>) {
    val pickedNumbers = mutableListOf<Int>()
    bingoBalls.forEach { picked ->
        pickedNumbers.add(picked)
        boards.forEach { board ->
            if (board.isWinner(pickedNumbers)) {
                println(board)
                println(pickedNumbers)
                println(board.score(pickedNumbers))
                println(pickedNumbers.last())
                println(board.score(pickedNumbers) * pickedNumbers.last())
                return
            }
        }
    }
}