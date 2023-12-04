import java.io.File

data class TokenLink(val token: Char, val last: TokenLink?)
class BlockParser {
    private val closerForOpener = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
    fun firstInvalidCloser(line: String): Char {
        return scan(line).second
    }

    fun blockClosers(line: String): List<Char> {
        var (nextCloser, _) = scan(line)
        var closers = listOf<Char>()
        while (nextCloser.last != null) {
            closers += nextCloser.token
            nextCloser = nextCloser.last!!
        }
        return closers
    }

    private fun scan(line: String): Pair<TokenLink, Char> {
        var nextWantedCloser: TokenLink? = TokenLink(' ', null)
        val errorToken = line.fold(' ') { bad_token, token ->
            if (bad_token != ' ') {
                bad_token
            } else if (token == nextWantedCloser!!.token) {
                nextWantedCloser = nextWantedCloser!!.last
                ' '
            } else if (closerForOpener.containsKey(token)) {
                nextWantedCloser = TokenLink(closerForOpener[token]!!, nextWantedCloser)
                ' '
            } else {
                token//.also(::print)
            }
        }
        return Pair(nextWantedCloser!!, errorToken)
    }
}

fun main() {
    val lines = File("syntax_chunks.txt").readLines()

    val parser = BlockParser()
    lines
        .filter { parser.firstInvalidCloser(it) != ' ' }
        .map { line -> parser.firstInvalidCloser(line) }
        .fold(0) { acc, token -> acc + invalidCloserScore(token) }
        .also(::println)

    val incompleteLines = lines.filter { parser.firstInvalidCloser(it) == ' ' }
    val lineCompleters = incompleteLines.map { parser.blockClosers(it) }
    val completerScores = lineCompleters.map(::completedLineScore)
    completerScores.sorted()[completerScores.size / 2].also(::println)
}

private fun completedLineScore(line: List<Char>) = line.fold(0L) { acc, token ->
    acc * 5 + completedCloserScore(token)
}

private fun completedCloserScore(token: Char) = when (token) {
    ')' -> 1
    ']' -> 2
    '}' -> 3
    '>' -> 4
    else -> 0
}

private fun invalidCloserScore(token: Char) = when (token) {
    ')' -> 3
    ']' -> 57
    '}' -> 1197
    '>' -> 25137
    else -> 0
}

