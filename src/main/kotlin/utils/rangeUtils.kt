package utils

/**
 * This will return a range that combine the 2 range.
 * It may result in one or more items since the 2 ranges may not overlap.
 * The assumption is that ranges have an end that is the same or a higher value than the start.
 */
fun IntRange.intersect(r: IntRange): List<IntRange> {
  val minR = if (first > r.first) r else this
  val maxR = if (first > r.first) this else r

  return when {
    minR.first <= maxR.first && minR.last >= maxR.last -> listOf(minR)
    maxR.first <= minR.first && maxR.last >= minR.last -> listOf(maxR)
    minR.first < maxR.first && minR.last < maxR.first -> listOf(minR, maxR)
    minR.last >= maxR.first && minR.last <= maxR.last -> listOf(minR.first..maxR.last)
    else -> error("Invalid $minR, $maxR")
  }
}

/**
 * This removes a specific value from the range.
 * This may result in more than one result if the input does fall inside the range excluding the start or end.
 * The assumption is that ranges have an end that is the same or a higher value than the start.
 */
fun IntRange.exclude(input: Int): List<IntRange> = if (input in this) {
  val split = mutableListOf<IntRange>()
  if((first == input) && (last == input)) {
    // do nothing
  } else if (first == input) {
    split.add((input + 1)..last)
  } else if (last == input) {
    split.add(first until input)
  } else {
    if (first < input + 1)
      split.add(first until input)
    if (input + 1 < last) {
      split.add(input + 1..last)
    }
  }
  split.toList()
} else {
  listOf(this)
}

/**
 * This will combine overlapping ranges and return a list with the smallest number of ranges that satisfies the requirements.
 * The assumption is that ranges have an end that is the same or a higher value than the start.
 */
fun joinRanges(ranges: List<IntRange>): List<IntRange> {
  if (ranges.isEmpty()) return emptyList()
  val result = mutableSetOf<IntRange>()
  result.addAll(ranges)
  val drop = mutableSetOf<IntRange>()

  fun intersectRange(range: IntRange) {
    val input = result.sortedBy { it.first }.toList()
    input.forEach {
      val lst = it.intersect(range).toSet()
      result.addAll(lst)
      if (!lst.contains(range)) {
        drop.add(range)
      }
      if (!lst.contains(it)) {
        drop.add(it)
      }
    }
    result.removeAll(drop)
  }
  ranges.forEach { range ->
    intersectRange(range)
  }
  do {
    val before = result.toSet()
    intersectRange(result.maxByOrNull { it.first }!!)
  } while (result != before)

  return result.sortedBy { it.first }
}
