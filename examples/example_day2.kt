import java.io.File

fun main() {
    val instructions = File("sub_instructions.txt").readLines().map { line -> line.split(" ") }
    val (depth1, position1) = instructions.fold(arrayOf(0, 0)) { (depth, position), (instruction, value_as_str) ->
        val value = value_as_str.toInt()
        when (instruction) {
            "forward" -> arrayOf(depth, position + value)
            "up" -> arrayOf(depth - value, position)
            "down" -> arrayOf(depth + value, position)
            else -> arrayOf(depth, position)
        }
    }
    println(depth1)
    println(position1)
    println(depth1 * position1)

    val (depth2, position2, _) = instructions.fold(arrayOf(0, 0, 0)) { (depth, position, aim), (instruction, value_as_str) ->
        val value = value_as_str.toInt()
        when (instruction) {
            "forward" -> arrayOf(depth + aim * value, position + value, aim)
            "up" -> arrayOf(depth, position, aim - value)
            "down" -> arrayOf(depth, position, aim + value)
            else -> arrayOf(depth, position, aim)
        }
    }
    println(depth2)
    println(position2)
    println(depth2 * position2)

}