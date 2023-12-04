import java.io.File
import kotlin.math.min
import kotlin.math.pow

class CachingHistogramCalculator(
    private val iterationLevel: Int,
    private val expansionLevel: Int,
    private val rules: Array<CharArray>
) {
    private val iterationLevelHistogramForPair = mutableMapOf<Pair<Char, Char>, Map<Char, Long>>()
    private val iterationLevelExpansionForPair = mutableMapOf<Pair<Char, Char>, List<Char>>()
    private val nextLevelCache = run {
        val furtherIterationsNeeded = iterationLevel - expansionLevel
        if (furtherIterationsNeeded > 0) CachingHistogramCalculator(
            furtherIterationsNeeded,
            min(furtherIterationsNeeded, expansionLevel),
            rules
        )
        else
            null
    }

    fun histogramForElements(elements: Pair<Char, Char>): Map<Char, Long> {
        if (iterationLevelHistogramForPair.containsKey(elements)) return iterationLevelHistogramForPair[elements]!!

        val histogram = mutableMapOf<Char, Long>()
        val workingChain = expandedChainForPair(elements, expansionLevel)
        if (nextLevelCache == null) {
            workingChain.dropLast(1).forEach { histogram[it] = histogram.getOrDefault(it, 0) + 1 }
        } else {
            workingChain.asSequence().zipWithNext().forEach {
                nextLevelCache.histogramForElements(it).forEach { (element, count) ->
                    histogram[element] = histogram.getOrDefault(element, 0) + count
                }
            }
        }
        return histogram.also { iterationLevelHistogramForPair[elements] = histogram }
    }

    private fun expandedChainForPair(elements: Pair<Char, Char>, expansionLevel: Int): List<Char> {
        if (iterationLevelExpansionForPair.containsKey(elements))
            return iterationLevelExpansionForPair[elements]!!

        if (nextLevelCache != null && nextLevelCache.cachesExpansionsAtLevel(expansionLevel))
            return nextLevelCache.expandedChainForPair(elements, expansionLevel)

        return expandChainForPair(elements.first, elements.second, expansionLevel, rules).also {
            iterationLevelExpansionForPair[elements] = it
        }
    }

    private fun cachesExpansionsAtLevel(desiredExpansionLevel: Int): Boolean {
        return expansionLevel == desiredExpansionLevel
                && iterationLevel == desiredExpansionLevel
                || (nextLevelCache == null || !nextLevelCache.cachesExpansionsAtLevel(desiredExpansionLevel))
    }
}

fun main() {
    val polymerFileLines = File("polymer_rules.txt").readLines()
    val template = polymerFileLines.first().toList()

    val polymerRuleRegEx = "(\\w\\w) -> (\\w)".toRegex()
    val polymerRulesMap = polymerFileLines.drop(2).associate {
        val (_, elementPair, insertionValue) = polymerRuleRegEx.find(it)!!.groupValues
        Pair(elementPair.toList(), insertionValue.toList().single())
    }
    val maxElementIndex = polymerRulesMap.keys.flatten().maxOf { it } + 1
    val polymerRules = Array(maxElementIndex.toInt()) { CharArray(maxElementIndex.toInt()) { ' ' } }
    polymerRulesMap.forEach {
        polymerRules[it.key.first().toInt()][it.key.last().toInt()] = it.value
    }

    val chainAfter10 = (1..10).fold(template) { chain, _ ->
        applyInsertionRules(chain, polymerRules)
    }.also(::println)
    val elementFrequencies = chainAfter10.groupingBy { it }.eachCount()
    val sortedFrequencies = elementFrequencies.toList().sortedBy { it.second }.also(::println)
    println(sortedFrequencies.last().second - sortedFrequencies.first().second)

    val calculator = CachingHistogramCalculator(40, 10, polymerRules)
    val elementFrequenciesAt40 = mutableMapOf<Char, Long>()
    template.asSequence().zipWithNext().forEach { elementPair ->
        calculator.histogramForElements(elementPair).forEach { (element, count) ->
            elementFrequenciesAt40[element] = elementFrequenciesAt40.getOrDefault(element, 0) + count
        }
    }
    elementFrequenciesAt40[template.last()] = elementFrequenciesAt40.getOrDefault(template.last(), 0) + 1
    val sortedFrequenciesAt10 = elementFrequenciesAt40.toList().sortedBy { it.second }.also(::println)
    println(sortedFrequenciesAt10.last().second - sortedFrequenciesAt10.first().second)

    println(sizeAtIteration(40, 20))
    println(elementFrequenciesAt40.values.sum())

}

private fun sizeAtIteration(iterationCount: Int, initialSize: Int): Long {
    val powerOf2 = 2.0.pow(iterationCount.toDouble()).toLong()
    return powerOf2 * initialSize - powerOf2 + 1
}

private fun expandChainForPair(first: Char, last: Char, iterations: Int, rules: Array<CharArray>): List<Char> {
    val chain = blankChainToHoldInsertions(iterations)
    iterateInsertionRulesIntoChain(chain, first, last, rules)
    return chain
}

private fun blankChainToHoldInsertions(iterationCount: Int) =
    MutableList(sizeAtIteration(iterationCount, 2).toInt()) { ' ' }

// Calculate the exact final position of each inserted element based on the total number of iterations
// and put it there as soon as it's calculated
private fun iterateInsertionRulesIntoChain(
    chain: MutableList<Char>,
    first: Char,
    last: Char,
    rules: Array<CharArray>
) {
    val lastIndex = chain.size - 1

    chain[0] = first
    chain[lastIndex] = last

    var step = lastIndex
    while (step > 1) {
        var index = step
        while (index <= lastIndex) {
            val charAtStart = chain[index - step].toInt()
            val charAtEnd = chain[index].toInt()
            val centerOfInterval = index - (step / 2)
            chain[centerOfInterval] = rules[charAtStart][charAtEnd]
            index += step
        }
        step /= 2
    }
}

private fun applyInsertionRules(chain: List<Char>, polymerRules: Array<CharArray>): List<Char> {
    return chain.zipWithNext().flatMap {
        listOf(it.first, polymerRules[it.first.toInt()][it.second.toInt()])
    }.plus(chain.last())
}
