fun main() {
  fun findCalories(input: List<String>): List<Int> {
    val elves = mutableListOf<Int>()
    var calories = 0
    for (line in input) {
      if (line.isBlank()) {
        elves.add(calories)
        calories = 0
      } else {
        calories += line.toInt()
      }
    }
    elves.add(calories)
    return elves
  }

  fun findMaxCalories(input: List<String>): Int {
    val calories = findCalories(input)
    return calories.max()
  }

  fun topThree(input: List<String>): Int {
    val calories = findCalories(input)
    return calories.sortedDescending().take(3).sum()
  }
  val testText = """1000
2000
3000

4000

5000
6000

7000
8000
9000

10000"""
  fun part1() {
    val testInput = readText(testText)
    val testMax = findMaxCalories(testInput)
    println("Part 1 - Test Max Calories = $testMax")
    check(testMax == 24000)
    val input = readFile("day01")
    val maxCalories = findMaxCalories(input)
    println("Part 1 - Max Calories = $maxCalories")
    check(maxCalories == 71300)
  }

  fun part2() {
    val testInput = readText(testText)
    val testTop3 = topThree(testInput)
    println("Part 2 - Test Top3 = $testTop3")
    check(testTop3 == 45000)
    val input = readFile("day01")
    val top3 = topThree(input)
    println("Part 2 - Top3 = $top3")
    check(top3 == 209691)
  }
  part1()
  part2()
}
