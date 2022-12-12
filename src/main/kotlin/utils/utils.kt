package utils

import java.io.File
import java.math.BigInteger

/**
 * Reads lines from the given input txt file.
 * @param name The name of the file to read
 */
fun readFile(name: String) = File("src/main/resources", "$name.txt").readLines()

/**
 * Reads whole file into a text string
 * @param name The name of the file to read
 */
fun readFileToString(name: String) = File("src", "$name.txt").readText()

/**
 * Read file into lines and group after condition is met place following lines into new list
 * @param name The name of the file to read
 * @param condition A lambda to evaluate to determine the grouping condition. The line will be excluded from the output. The default will be a blank line
 */
fun readFileGroup(name: String, condition: (String) -> Boolean = { it.isBlank() }): List<List<String>> {
  val input = File("src", "$name.txt").readLines()
  val result = mutableListOf<List<String>>()
  val list = mutableListOf<String>()
  input.forEach {
    if (condition(it)) {
      result.add(list.toList())
      list.clear()
    } else {
      list.add(it)
    }
  }
  if (list.isNotEmpty()) {
    result.add(list.toList())
  }
  return result.toList()
}

/**
 * Parses input text as lines and removes trailing whitespace like \r
 * @param text This is typically text that has been declared between """ or has embedded \n
 */
fun readLines(text: String) = text.split("\n").map { it.trimEnd() }

/**
 * Parse text into lines and group after condition is met place following lines into new list
 * @param text The text to parse
 * @param condition A lambda to evaluate to determine the grouping condition. The line will be excluded from the output. The default will be a blank line
 */
fun readLinesGroup(text: String, condition: (String) -> Boolean = { it.isBlank() }): List<List<String>> {
  val input = readLines(text)
  val result = mutableListOf<List<String>>()
  val list = mutableListOf<String>()
  input.forEach {
    if (condition(it)) {
      result.add(list.toList())
      list.clear()
    } else {
      list.add(it)
    }
  }
  if (list.isNotEmpty()) {
    result.add(list.toList())
  }
  return result.toList()
}


fun separator() {
  (1..50).forEach { _ -> print('=') }
  println()
}


// found on https://rosettacode.org/wiki/Permutations#Kotlin
fun <T> permute(input: List<T>): List<List<T>> {
  if (input.isEmpty()) return emptyList()
  if (input.size == 1) return listOf(input)
  val perms = mutableListOf<List<T>>()
  val toInsert = input[0]
  for (perm in permute(input.drop(1))) {
    for (i in 0..perm.size) {
      val newPerm = perm.toMutableList()
      newPerm.add(i, toInsert)
      perms.add(newPerm)
    }
  }
  return perms
}

fun permutationsBI(n: BigInteger): BigInteger =
  n * if (n > BigInteger.ONE) permutationsBI(n - BigInteger.ONE) else BigInteger.ONE

fun permutations(n: Int): BigInteger = permutationsBI(n.toBigInteger())

fun <T> permuteInvoke(input: List<T>, handlePermutation: (List<T>) -> Unit) {
  if (input.size == 1) {
    handlePermutation(input)
  } else {
    val toInsert = input[0]
    permuteInvoke(input.drop(1)) { perm ->
      for (i in 0..perm.size) {
        val newPerm = perm.toMutableList()
        newPerm.add(i, toInsert)
        handlePermutation(newPerm)
      }
    }
  }
}

fun <T> permuteArray(input: Array<T>, makeArray: (Collection<T>) -> Array<T>): List<Array<T>> {
  if (input.isEmpty()) return emptyList()
  if (input.size == 1) return listOf(input)
  val perms = mutableListOf<Array<T>>()
  val toInsert = input[0]
  for (perm in permuteArray(makeArray(input.drop(1)), makeArray)) {
    for (i in 0..perm.size) {
      val newPerm = perm.toMutableList()
      newPerm.add(i, toInsert)
      perms.add(makeArray(newPerm))
    }
  }
  return perms
}

class Combinations(val m: Int, val n: Int) {
  private val combination = IntArray(m)
  val combinations: MutableList<IntArray> = mutableListOf()

  init {
    generate(0)
  }

  private fun generate(k: Int) {
    if (k >= m) {
      combinations.add(combination.toList().toIntArray())
    } else {
      for (j in 0 until n)
        if (k == 0 || j > combination[k - 1]) {
          combination[k] = j
          generate(k + 1)
        }
    }
  }
}


class ProcessingState(val registers: List<Char>, val processing: ProcessingState.() -> Unit) {
  var clock = 0
    private set
  private val register = registers.associateWith { 0 }.toMutableMap()
  fun tick() {
    clock += 1
    processing(this)
  }
  operator fun get(reg: Char): Int = register[reg]?: error("Invalid register $reg")
  operator fun set(reg: Char, value: Int) {
    register[reg] = value
  }
  var regX: Int
    get() = get('X')
    set(value:Int) {
      register['X'] = value
    }
}


