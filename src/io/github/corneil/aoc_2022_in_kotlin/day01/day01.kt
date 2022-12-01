package io.github.corneil.aoc_2022_in_kotlin.day01
import io.github.corneil.aoc_2022_in_kotlin.utils.readInput

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

    val testInput = readInput("day01_test1")
    val testMax = findMaxCalories(testInput)
    println("Test Max Calories = $testMax")
    check(testMax == 24000)
    val input = readInput("day01")
    val maxCalories = findMaxCalories(input)
    println("Max Calories = $maxCalories")

    val testTop3 = topThree(testInput)
    println("Test Top3 = $testTop3")
    val top3 = topThree(input)
    println("Top3 = $top3")
}
