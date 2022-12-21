package day00

import main.utils.measureAndPrint
import kotlin.collections.*
import utils.*

fun main() {

  val testLines = readLines("""""")
  val lines = readFile("day00")

  fun calcSolution(input: List<String>): Int = 0

  fun part1() {
    val testResult = measureAndPrint("Part 1 Test Time: ") {  calcSolution(testLines) }
    println("Part 1 Test Answer = $testResult")
    checkNumber(testResult, 0)
    val result = measureAndPrint("Part 1 Time: ") {  calcSolution(lines) }
    println("Part 1 Answer = $result")
//    checkNumber(result, 0)
  }

  fun part2() {
//    val testResult = measureAndPrint("Part 2 Test Time: ") { calcSolution(testLines) }
//    println("Part 2 Test Answer = $testResult")
//    checkNumber(testResult, 0)
//    val result = measureAndPrint("Part 2 Time: ") { calcSolution(lines) }
//    println("Part 2 Answer = $result")
//    checkNumber(result, 0)
  }
  println("Day - 00")
  part1()
  separator()
  part2()
}
