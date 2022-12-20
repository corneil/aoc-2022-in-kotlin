package day20

import main.utils.measureAndPrint
import main.utils.scanInt
import utils.checkNumber
import utils.readFile
import utils.readLines
import utils.separator
import java.util.*

fun main() {

  val test = readLines(
    """1
2
-3
3
-2
0
4"""
  )
  val lines = readFile("day20")


  class EncryptedFile(val input: List<Int>) {
    private fun mix(number: Pair<Long, Int>, mixed: MutableList<Pair<Long, Int>>) {
      if (number.first != 0L) {
        val index = mixed.indexOf(number)
        check(index >= 0)
        mixed.remove(number)
        Collections.rotate(mixed, ((-1L * number.first) % mixed.size.toLong()).toInt())
        mixed.add(index, number)
      }
    }

    fun numbersAfterZeroMixed(rounds: List<Int>, mixCount: Int, encryptionKey: Long): List<Long> {
      val zeroInputIndex = input.indexOf(0)
      val indexedList = input.mapIndexed { index, value -> encryptionKey * value.toLong() to index }.toList()
      val mixedFile = indexedList.toMutableList()
      check(indexedList.size == input.size)
      check(mixedFile.size == input.size)
      repeat(mixCount) { count ->
        indexedList.forEach { number ->
          mix(number, mixedFile)
        }
        check(mixedFile.size == input.size) { "Sizes differ for mixed ${mixedFile.size} and input ${input.size} after $count" }
      }
      return rounds.map { round ->
        val zeroIndex = mixedFile.indexOf(0L to zeroInputIndex)
        check(zeroIndex >= 0) { "Expected zeroIndex >=0 not $zeroIndex" }
        val end = (zeroIndex + (round % input.size)) % input.size
        mixedFile[end].first
      }
    }
  }

  fun calcSolution(input: List<String>, mixCount: Int = 1, encryptionKey: Long = 1, print: Boolean = true): Long {
    val file = input.map { it.scanInt() }
    val encrypted = EncryptedFile(file)
    val values = encrypted.numbersAfterZeroMixed(listOf(1000, 2000, 3000), mixCount, encryptionKey)
    if (print) {
      println("Values=${values.joinToString(", ")}")
    }
    return values.sum()
  }

  fun part1() {
    println("Warmup.")
    repeat(1000) { calcSolution(test, 1, 1, false) }
    println("Start.")
    val testResult = measureAndPrint("Part 1 Test Time: ") {
      calcSolution(test)
    }
    println("Part 1 Test Answer = $testResult")
    checkNumber(testResult, 3L)
    val result = measureAndPrint("Part 1 Time: ") {
      calcSolution(lines)
    }
    println("Part 1 Answer = $result")
    checkNumber(result, 4426L)
  }

  fun part2() {
    val encryptionKey = 811589153L

    val testResult = measureAndPrint("Part 2 Test Time: ") {
      calcSolution(test, 10, encryptionKey)
    }
    println("Part 2 Test Answer = $testResult")
    checkNumber(testResult, 1623178306L)

    val result = measureAndPrint("Part 2 Time: ") {
      calcSolution(lines, 10, encryptionKey)
    }
    println("Part 2 Answer = $result")
  }
  println("Day - 20")
  part1()
  separator()
  part2()
}
