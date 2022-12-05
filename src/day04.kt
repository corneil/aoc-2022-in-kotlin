fun main() {
  fun convertRange(input: String): IntRange {
    val values = input.split("-")
    return values[0].toInt()..values[1].toInt()
  }

  fun convertRanges(input: List<String>): List<Pair<Set<Int>, Set<Int>>> =
    input.map { it.split(",") }
      .map { Pair(convertRange(it[0]).toSet(), convertRange(it[1]).toSet()) }

  fun calcContains(ranges: List<Pair<Set<Int>, Set<Int>>>): Int {
    return ranges.count {
      it.first.containsAll(it.second) || it.second.containsAll(it.first)
    }
  }

  fun calcOverlap(ranges: List<Pair<Set<Int>, Set<Int>>>): Int {
    return ranges.count { it.first.intersect(it.second).isNotEmpty() }
  }

  val test = """2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8"""

  fun part1() {
    val testCount = calcContains(convertRanges(readText(test)))
    println("Part 1 Test Count = $testCount")
    check(testCount == 2)

    val count = calcContains(convertRanges(readFile("day04")))
    println("Part 1 Count = $count")
    check(count == 524)
  }

  fun part2() {
    val testCount = calcOverlap(convertRanges(readText(test)))
    println("Part 2 Test Count = $testCount")
    check(testCount == 4)

    val count = calcOverlap(convertRanges(readFile("day04")))
    println("Part 2 Count = $count")
    check(count == 798)
  }
  part1()
  part2()
}
