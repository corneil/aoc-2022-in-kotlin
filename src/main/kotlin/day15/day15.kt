package day15

import main.utils.measureAndPrint
import main.utils.scanInts
import utils.*
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.math.max

fun main() {

  val test = readLines(
    """
    Sensor at x=2, y=18: closest beacon is at x=-2, y=15
    Sensor at x=9, y=16: closest beacon is at x=10, y=16
    Sensor at x=13, y=2: closest beacon is at x=15, y=3
    Sensor at x=12, y=14: closest beacon is at x=10, y=16
    Sensor at x=10, y=20: closest beacon is at x=10, y=16
    Sensor at x=14, y=17: closest beacon is at x=10, y=16
    Sensor at x=8, y=7: closest beacon is at x=2, y=10
    Sensor at x=2, y=0: closest beacon is at x=2, y=10
    Sensor at x=0, y=11: closest beacon is at x=2, y=10
    Sensor at x=20, y=14: closest beacon is at x=25, y=17
    Sensor at x=17, y=20: closest beacon is at x=21, y=22
    Sensor at x=16, y=7: closest beacon is at x=15, y=3
    Sensor at x=14, y=3: closest beacon is at x=15, y=3
    Sensor at x=20, y=1: closest beacon is at x=15, y=3
  """.trimIndent()
  )
  val input = readFile("day15")

  class Grid(val cells: MutableMap<Coord, Char> = mutableMapOf()) {
    fun print() {
      val zX = cells.keys.maxOf { it.x }
      val aX = cells.keys.minOf { it.x }
      val zY = cells.keys.maxOf { it.y }
      val aY = cells.keys.minOf { it.y }
      println("X = $aX - $zX, Y=$aY - $zY")
      for (y in aY..zY) {
        print("%4d ".format(y))
        for (x in aX..zX) {
          val pos = Coord(x, y)
          print(cells.getOrDefault(pos, '.'))
        }
        println()
      }
    }
  }

  data class Sensor(val sensor: Coord, val beacon: Coord) {
    val distance = sensor.chebyshevDistance(beacon)
    fun isInRange(pos: Coord) = sensor.chebyshevDistance(pos) <= distance
    fun deadSpots(row: Int): IntRange? {
      val distanceToRow = abs(row - sensor.y)
      return if (distanceToRow <= distance) {
        val diff = distance - distanceToRow
        (sensor.x - diff)..(sensor.x + diff)
      } else {
        null
      }
    }
  }

  fun printGrid(sensors: List<Sensor>) {
    val grid = mutableMapOf<Coord, Char>()
    sensors.forEach { sensor ->
      grid[sensor.sensor] = 'S'
      grid[sensor.beacon] = 'B'
    }
    for (sensor in sensors) {
      val start = sensor.sensor.x - sensor.distance
      val end = sensor.sensor.x + sensor.distance
      val rows = (sensor.sensor.y - sensor.distance)..(sensor.sensor.y + sensor.distance)
      for (y in rows) {
        for (x in start..end) {
          val pos = Coord(x, y)
          if (sensor.isInRange(pos)) {
            if (!grid.containsKey(pos)) {
              grid[pos] = '#'
            }
          }
        }
      }
    }
    Grid(grid).print()
  }

  fun loadSensors(input: List<String>): List<Sensor> {
    val result = mutableMapOf<Coord, Char>()
    input.forEach { line ->
      line.scanInts().chunked(4) { (a,b,c,d) ->
        result[Coord(a,b)] = 'S'
        result[Coord(c,d)] = 'B'
      }
    }
    val beacons = result.filter { it.value == 'B' }.map { it.key }
    return result.filter { it.value == 'S' }
      .map { it.key }
      .map { sensor ->
      val beacon = beacons.map {
        it to sensor.chebyshevDistance(it)
      }.minBy { it.second }.first
      Sensor(sensor, beacon)
    }
  }

  fun calcDeadSpots(sensors: List<Sensor>, row: Int): Int {
    val beacons = sensors.map { it.beacon }
      .filter { it.y == row }
      .map { it.x }
      .toSet()

    val deadSpots = sensors.filter {
      it.sensor.y <= row + it.distance && it.sensor.y >= row - it.distance
    }.mapNotNull { it.deadSpots(row) }
      .flatMap { r ->
      beacons.flatMap { beacon ->
        r.exclude(beacon)
      }
    }
    return joinRanges(deadSpots).sumOf { it.last - it.first + 1 }
  }


  fun calcBeaconFrequency(sensors: List<Sensor>, size: Int): Long {
    val range = 0..size
    val beacons = sensors.map { it.beacon }.toSet()
    val minRow = sensors.minOfOrNull { min(it.sensor.y, it.beacon.y) - it.distance } ?: 0
    val maxRow = sensors.maxOfOrNull { max(it.sensor.y, it.beacon.y) + it.distance } ?: size
    val rows = max(minRow, 0)..min(maxRow, size)
    println("rows = $rows")
    for (row in rows) {
      val deadSpots = sensors
        .mapNotNull { it.deadSpots(row) }
        .filter { it.first in range || it.last in range }
        .sortedBy { it.first }
      val combined = joinRanges(deadSpots)
        .map { max(it.first, 0)..min(it.last, size) }
      for (index in 1..combined.lastIndex) {
        val a = combined[index - 1]
        val b = combined[index]
        val searchRange = (a.last + 1) until b.first
        for (x in searchRange) {
          if (x in range) {
            val pos = Coord(x, row)
            val found = !beacons.contains(pos) && sensors.filter {
              it.sensor.y <= row + it.distance && it.sensor.y >= row - it.distance
            }.none { it.isInRange(pos) }
            if (found) {
              return pos.x.toLong() * size.toLong() + pos.y.toLong()
            }
          }
        }
      }
    }
    return error("Hidden Beacon not found")
  }

  fun calcSolution1(input: List<String>, row: Int, print: Boolean): Int {
    val sensors = loadSensors(input)
    if (print) {
      printGrid(sensors)
    }
    return calcDeadSpots(sensors, row)
  }


  fun calcSolution2(input: List<String>, size: Int, print: Boolean): Long {
    val sensors = loadSensors(input)
    if (print) {
      printGrid(sensors)
    }
    return calcBeaconFrequency(sensors, size)
  }

  fun part1() {
    val testResult = calcSolution1(test, 10, true)
    println("Part 1 Test Answer = $testResult")
    check(testResult == 26) { "Expected 26 not $testResult" }
    val result = measureAndPrint("Part 1 Time:") {
      calcSolution1(input, 2000000, false)
    }
    println("Part 1 Answer = $result")
    check(result == 4502208) { "Expected 4502208 not $result" }
  }

  fun part2() {
    val testResult = measureAndPrint("Part 2 Test Time:") {
      calcSolution2(test, 4000000, true)
    }
    println("Part 2 Test Answer = $testResult")
    check(testResult == 56000011L) { "Expected 56000011 not $testResult" }
    val result = measureAndPrint("Part 2 Time:") {
      calcSolution2(input, 4000000, false)
    }
    println("Part 2 Answer = $result")
    check(result == 13784551204480L) { "Expected 13784551204480 not $result" }
  }
  println("Day - 15")
  part1()
  part2()
}
