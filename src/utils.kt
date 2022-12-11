package utils

import java.io.File
import java.math.BigDecimal
import java.util.*
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.system.measureNanoTime

/**
 * Reads lines from the given input txt file.
 * @param name The name of the file to read
 */
fun readFile(name: String) = File("src", "$name.txt").readLines()

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

class Stack<T>(private val elements: MutableList<T> = mutableListOf<T>()) {
  fun empty(): Boolean = elements.isEmpty()
  fun peek(): T = elements[elements.lastIndex]
  fun pop(): T = elements.removeAt(elements.lastIndex)
  fun push(value: T) {
    elements.add(value)
  }
  fun clear() = elements.clear()
  fun items() = elements.toList()
  fun size() = elements.size
}

inline fun <reified T> Collection<T>.filterUnique(): Collection<T> {
  val visiting = mutableSetOf<T>()
  return this.filter {
    if (visiting.contains(it)) {
      false
    } else {
      visiting.add(it)
      true
    }
  }
}


/**
 * This Graph is stateful and cannot be shared.
 * Maintain the collections of edges and use when needed.
 */
data class Edge<T>(val c1: T, val c2: T, val distance: Int)
class Graph<T : Comparable<T>>(edges: Collection<Edge<T>>, directed: Boolean) {
  class Vertex<T : Comparable<T>>(
    val key: T,
    var distance: Int = Int.MAX_VALUE,
    var prev: Vertex<T>? = null,
    val neighbours: MutableMap<Vertex<T>, Int> = mutableMapOf()
  ) : Comparable<Vertex<T>> {
    override fun compareTo(other: Vertex<T>): Int {
      var result = distance.compareTo(other.distance)
      if (result == 0) {
        result = key.compareTo(other.key)
      }
      return result
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Vertex<*>

      if (key != other.key) return false
      if (distance != other.distance) return false

      return true
    }

    override fun hashCode(): Int {
      var result = key.hashCode()
      result = 31 * result + distance
      return result
    }

    fun findPath(path: MutableList<Pair<T, Int>>) {
      if (this == prev) {
        path.add(Pair(key, distance))
      } else if (prev != null) {
        prev!!.findPath(path)
        path.add(Pair(key, distance))
      }
    }

    fun findNeighbours(path: MutableList<Pair<T, Int>>, depth: Int, matcher: (T) -> Boolean): Boolean {
      return if(depth >= 0) {
        val valid = neighbours.filter {
          matcher(it.key.key)
        }.filter {
          it.key.findNeighbours(path, depth - 1, matcher)
        }.map { it.key.key to it.value + depth }
        path.addAll(valid)
        true
      } else
        false
    }
  }

  private val graph = HashMap<T, Vertex<T>>(edges.size)
  init {
    for (e in edges) {
      if (!graph.containsKey(e.c1)) {
        graph[e.c1] = Vertex(e.c1)
      }
      if (!graph.containsKey(e.c2)) {
        graph[e.c2] = Vertex(e.c2)
      }
    }

    // another pass to set neighbouring vertices
    for (e in edges) {
      graph[e.c1]!!.neighbours[graph[e.c2]!!] = e.distance
      if (!directed) {
        graph[e.c2]!!.neighbours[graph[e.c1]!!] = e.distance
      }
    }
  }

  /**
   * Set the starting point for a findPath
   */
  fun dijkstra(start: T) {
    if (!graph.containsKey(start)) {
      error("Cannot find start $start")
    }
    val source = graph[start]
    val q = TreeSet<Vertex<T>>()

    // set-up vertices
    for (v in graph.values) {
      v.prev = if (v == source) source else null
      v.distance = if (v == source) 0 else Int.MAX_VALUE
      q.add(v)
    }

    dijkstra(q)
  }

  private fun dijkstra(q: TreeSet<Vertex<T>>) {
    while (!q.isEmpty()) {

      val u = q.pollFirst()
      if (u.distance == Int.MAX_VALUE) break
      for (a in u.neighbours) {
        val v = a.key

        val alternateDist = u.distance + a.value
        if (alternateDist < v.distance) {
          q.remove(v)
          v.distance = alternateDist
          v.prev = u
          q.add(v)
        }
      }
    }
  }

  fun findNeighbours(start: T, depth: Int, matches: (T) -> Boolean): Set<Pair<T, Int>> {
    dijkstra(start)
    val path = mutableListOf<Pair<T, Int>>()
    graph[start]!!.findNeighbours(path, depth, matches)
    return path.toSet()
  }

  /**
   * Set starting point and determine the shortest path
   */
  fun findPath(start: T, end: T): List<Pair<T, Int>> {
    dijkstra(start)
    return findPath(end)
  }

  /**
   * Find the shortest path from start to end. That last pair.second will also be to total distance
   */
  fun findPath(end: T): List<Pair<T, Int>> {
    if (!graph.containsKey(end)) {
      error("Cannot find $end")
    }
    val path = mutableListOf<Pair<T, Int>>()
    graph[end]!!.findPath(path)
    return path.toList()
  }
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

val dividers = listOf(
  BigDecimal.valueOf(1000L) to "µs",
  BigDecimal.valueOf(1000_000L) to "ms",
  BigDecimal.valueOf(1000_000_000L) to "s"
)
fun roundTime(measurement: Long, divide: BigDecimal): BigDecimal {
  return (BigDecimal.valueOf(measurement).setScale(12) / divide).setScale(3, RoundingMode.CEILING)
}
fun printTime(prefix: String, measurement: Long) {
  val measuredTime = dividers.map { roundTime(measurement, it.first) to it.second }
    .filterIndexed { index, pair -> index == dividers.lastIndex || pair.first < BigDecimal.valueOf(1000L) }
    .first()
  println("$prefix${measuredTime.first}${measuredTime.second}")
}
fun <T> measureAndPrint(prefix: String = "Time: ", work: () -> T): T {
  var result: T? = null
  val measurement = measureNanoTime {
    result = work()
  }
  // find the times that have less than three significant digits on the left of decimal. Keep the last. Use the first
  printTime(prefix, measurement)
  return result ?: error("Expected result!")
}
