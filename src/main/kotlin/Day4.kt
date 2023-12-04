import java.io.File
import kotlin.math.min
import kotlin.math.pow

fun main() {
    val cardFile = "scratch_cards.txt"
    val cardRegEx = "[^:]+:([^|]+)\\|(.*)".toRegex()
    var cardCount = 0

    val cards = File(cardFile).readLines().map { cardData ->
        val (_, winners, haves) = cardRegEx.find(cardData)!!.groupValues
        ScratchCard(
            cardCount++,
            winners.splitOnSpaces().toSet(),
            haves.splitOnSpaces().toSet(),
        )
    }

    part1(cards)
    part2(cards)
}

private fun part1(cards: List<ScratchCard>) {
    cards.fold(0) { acc, card ->
        acc + card.value()
    }.also(::println)
}

fun part2(cards: List<ScratchCard>) {
    cards.reversed().forEach { it.calculateCardsWon(cards) }
    print("${cards.fold(0){ total, card -> total + card.cardsWon }}")
}

data class ScratchCard(
    val cardNumber: Int,
    val winners: Set<String>,
    val haves: Set<String>,
    var cardsWon: Int = 1,
) {
    private fun matches() = haves.intersect(winners)
    fun value(): Int {
        return when (matches().count()) {
            0 -> 0
            else -> 2.0.pow(matches().count() - 1).toInt()
        }
    }

    fun calculateCardsWon(cards: List<ScratchCard>) {
        val start = cardNumber + 1
        val end = min(cardNumber + matches().count(), cards.indices.last)
        cardsWon = (start..end).fold(1) { total, index ->
            total + cards[index].cardsWon
        }
    }
}
