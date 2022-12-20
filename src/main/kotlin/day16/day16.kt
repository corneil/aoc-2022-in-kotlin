package day16

import main.utils.Edge
import main.utils.Graph
import main.utils.scanInt
import utils.readFile
import utils.readLines
import kotlin.math.max

fun main() {

  val test = readLines(
    """Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II"""
  )
  val input = readFile("day16")

  data class Cell(val name: String, val rate: Int) : Comparable<Cell> {
    var neighbours: Set<Pair<Cell, Int>> = emptySet()
    override fun compareTo(other: Cell): Int {
      return rate.compareTo(other.rate)
    }

    override fun toString(): String {
      return "($name, $rate, ${neighbours.map { it.first.name }})"
    }

  }

  fun loadCells(input: List<String>): List<Pair<Cell, List<String>>> {
    return input.map { line ->
      val (left, right) = line.split(";").map { it.trim() }
      val name = left.substringAfter("Valve ").substring(0..1)
      val rate = left.scanInt()
      val links = if (right.contains("tunnel leads to valve "))
        listOf(right.substringAfter("tunnel leads to valve ").trim())
      else
        right.substringAfter("valves ").split(",").map { it.trim() }
      Pair(Cell(name, rate), links)
    }
  }


  fun calcSolution(input: List<String>, minutes: Int, useElephant: Boolean): Int {
    val nodes = loadCells(input)
    val cells = nodes.map { it.first }.associateBy { it.name }
    val start = cells["AA"] ?: error("Cannot find AA")
    val edges: List<Edge<Cell>> = nodes.flatMap { node ->
      node.second.map { next ->
        cells[next] ?: error("Cannot find $next")
      }.map {
        Edge(node.first, it)
      }
    }
    val valves = cells.values.sortedByDescending { it.rate }
    val nonLeafNodes = cells.values.filter { it.rate > 0 }.sortedByDescending { it.rate }

    fun calculateDistances(): Map<Cell, Map<Cell, Int>> {
      val result = mutableMapOf<Cell, Map<Cell, Int>>()
      valves.forEach { start ->
        val graph = Graph(edges, true).apply { dijkstra(start) }
        val connections = nonLeafNodes.map { end ->
          end to (graph.findPath(end).lastOrNull()
            ?.second ?: Int.MAX_VALUE)
            .apply { println("Distance ${start.name} -> ${end.name} = $this") }
        }.toMap()
        result[start] = connections
      }

      return result
    }

    val distances = calculateDistances()


    var totalPressure = 0
    fun visitValves(releasedPressure: Int, position: Cell, visited: List<Cell>, minute: Int, elephant: Boolean) {
      totalPressure = max(releasedPressure, totalPressure)
      println("minute $minute, position:${position.name}: visited:${visited.map { it.name }}: releasePressure=$releasedPressure: total $totalPressure")

      if (!elephant) {
        val possiblePressureRelease = (valves - visited)
          .zip((minutes - minute - 2 downTo 0 step 2)) { futureValve, futureMinute ->
            futureValve.rate * futureMinute
          }.sum()
        if (possiblePressureRelease + releasedPressure < totalPressure) return
      }
      if (!elephant) {
        val unvisited = (valves - visited)
        var index = 0
        val remaining = minutes - minute downTo 0 step 2
        val release = remaining.sumOf { (unvisited.getOrNull(index++)?.rate ?: 0) * it }
        if ((release + releasedPressure) < totalPressure) return
      }
      distances.getValue(position).forEach { (node, distance) ->
        val nextMinute = minute + distance + 1
        if (nextMinute < minutes && !visited.contains(node)) {
          val candidatePressure = releasedPressure + ((minutes - nextMinute) * node.rate)
          visitValves(candidatePressure, node, visited + node, nextMinute, elephant)
        }
      }
      if (elephant) {
        visitValves(releasedPressure, start, visited, 0, false)
      }
    }


    println("Valves:")
    valves.forEach { println(it) }
    visitValves(0, start, listOf(), 0, useElephant)
    return totalPressure
  }

  fun part1() {
    val testResult = calcSolution(test, 30, false)
    println("Part 1 Answer = $testResult")
    check(testResult == 1651) { "Expected 1651 not $testResult" }
    val result = calcSolution(input, 30, false)
    println("Part 1 Answer = $result")
    check(result == 1559) { "Expected 1559 not $result" }
  }

  fun part2() {
    val testResult = calcSolution(test, 26, true)
    println("Part 1 Answer = $testResult")
    check(testResult == 1707) { "Expected 1707 not $testResult" }
    val result = calcSolution(input, 26, true)
    println("Part 1 Answer = $result")
    check(result == 2191) { "Expected 2191 not $result" }
  }
  println("Day - 16")
  part1()
  part2()
}
