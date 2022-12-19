package day18

import main.utils.filterUnique
import main.utils.scanInts
import utils.checkNumber
import utils.readFile
import utils.readLines
import utils.separator
import kotlin.math.round
import kotlin.math.sqrt

fun main() {

  val test = readLines(
    """2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5"""
  )
  val input = readFile("day18")

  data class Coord3D(val x: Int, val y: Int, val z: Int) : Comparable<Coord3D> {
    val surrounds: Set<Coord3D> by lazy {
      setOf(
        copy(x = x - 1), copy(x = x + 1),
        copy(y = y - 1), copy(y = y + 1),
        copy(z = z - 1), copy(z = z + 1)
      )
    }

    operator fun minus(other: Coord3D) = Coord3D(x - other.x, y - other.y, z - other.z)
    operator fun plus(other: Coord3D) = Coord3D(x + other.x, y + other.y, z + other.z)
    override fun compareTo(other: Coord3D): Int {
      val diff = this - other
      val square = (diff.x * diff.x) + (diff.y * diff.y) + (diff.z * diff.z)
      val sq = round(sqrt(square.toDouble())).toInt()
      return 0.compareTo(sq)
    }

    override fun toString(): String {
      return "Coord3D($x,$y,$z)"
    }

  }

  class Lava(val cubes: List<Coord3D>) {
    private val cubeSet = cubes.toSet()
    private val allXCaches = mutableMapOf<Pair<Int, Int>, List<Int>>()
    private val allYCaches = mutableMapOf<Pair<Int, Int>, List<Int>>()
    private val allZCaches = mutableMapOf<Pair<Int, Int>, List<Int>>()
    fun getAllX(y: Int, z: Int): List<Int> =
      allXCaches.getOrPut(Pair(y, z)) {
        cubes.filter { it.y == y && it.z == z }.map { it.x }.sorted()
      }

    fun getAllY(x: Int, z: Int): List<Int> =
      allYCaches.getOrPut(Pair(x, z)) {
        cubes.filter { it.x == x && it.z == z }.map { it.y }.sorted()
      }

    fun getAllZ(x: Int, y: Int): List<Int> =
      allZCaches.getOrPut(Pair(x, y)) {
        cubes.filter { it.x == x && it.y == y }.map { it.z }.sorted()
      }

    fun hasCube(loc: Coord3D) = cubeSet.contains(loc)
    fun isInside(loc: Coord3D): Boolean {
      if (hasCube(loc)) return true
      val allZ = getAllZ(loc.x, loc.y)
      if (allZ.isEmpty()) return false
      val allY = getAllY(loc.x, loc.z)
      if (allY.isEmpty()) return false
      val allX = getAllX(loc.y, loc.z)
      return allX.isNotEmpty()
        && loc.z in allZ.min()..allZ.max()
        && loc.x in allX.min()..allX.max()
        && loc.y in allY.min()..allY.max()
    }

    fun outSide(): List<Coord3D> {
      return cubes.flatMap { cube ->
        cube.surrounds.filter { !isInside(it) }
      }.filterUnique().toList()
    }


    val minX: Int = cubes.minOf { it.x }
    val minY: Int = cubes.minOf { it.y }
    val minZ: Int = cubes.minOf { it.z }
    val maxX: Int = cubes.maxOf { it.x }
    val maxY: Int = cubes.maxOf { it.y }
    val maxZ: Int = cubes.maxOf { it.z }
    val width: Int = maxX - minX
    val height: Int = maxY - minY
    val depth: Int = maxZ - minZ
    fun printCube(screen: Int = 0) {
      val columns = screen / width
      var col = 0
      println("== Cube: Depth=$depth, Height=$height, Width=$width ==")
      for (z in minZ..maxZ) {
        println()
        println("---- Z = %02d --------".format(z))
        col = 0
        for (y in minY..maxY) {
          print("%02d ".format(y))
          for (x in minX..maxX) {
            val loc = Coord3D(x, y, z)
            if (hasCube(loc)) {
              print('#')
            } else {
              if (isInside(loc)) {
                print('.')
              } else {
                print(' ')
              }
            }
          }
          print(' ')
          col++
          if (col >= columns) {
            col = 0
            println()
          }
        }
      }
      println()
      separator()
    }

    fun operateInside(operate: (Coord3D) -> Unit) {
      for (x in minX..maxX) {
        for (y in minY..maxY) {
          for (z in minZ..maxZ) {
            val loc = Coord3D(x, y, z)
            if (isInside(loc)) {
              operate(loc)
            }
          }
        }
      }
    }

    fun iterateInside(): Iterable<Coord3D> =
      (minX..maxX).flatMap { x ->
        (minY..maxY).flatMap { y ->
          (minZ..maxZ).mapNotNull { z ->
            val loc = Coord3D(x, y, z)
            if (isInside(loc)) loc else null
          }
        }
      }

    fun surfaceArea(): Int =
      cubes.sumOf { cube ->
        val surrounds = cube.surrounds
        surrounds.size - surrounds.count { hasCube(it) }
      }

  }


  fun scanLava(input: List<String>): List<Coord3D> =
    input.map { line ->
      val (x, y, z) = line.scanInts()
      Coord3D(x, y, z)
    }

  fun calcSolution1(input: List<String>): Int {
    return Lava(scanLava(input).toList()).surfaceArea()
  }

  fun createFilled(lavaCubes: List<Coord3D>): Lava {
    val lava = Lava(lavaCubes)
    val airPockets = mutableSetOf<Coord3D>()
    airPockets.addAll(lavaCubes)
    val lockedAir = lava.iterateInside().filter { loc ->
      !lava.hasCube(loc)
        && loc.surrounds.none { !lava.hasCube(it) && !lava.isInside(it) }
    }
    airPockets.addAll(lockedAir)
    println("Air Pockets = ${airPockets.size}")
    val filled = Lava(airPockets.toList())
    println("Filled has ${filled.cubes.size} blocks")
    return filled
  }

  fun calcSolution2(input: List<String>): Int {
    val lava = createFilled(scanLava(input))
    println("Total blocks = ${lava.cubes.size}")
    return lava.surfaceArea()
  }

  fun part1() {
    val testResult = calcSolution1(test)
    println("Part 1 Test Answer = $testResult")
    checkNumber(testResult, 64)
    val result = calcSolution1(input)
    println("Part 1 Answer = $result")
    checkNumber(result, 4242)
  }

  fun part2() {
    val testResult = calcSolution2(test)
    println("Part 2 Test Answer = $testResult")
    checkNumber(testResult, 58)
    val result = calcSolution2(input)
    println("Part 2 Answer = $result")
    checkNumber(result, 2428)
  }
  println("Day - 18")
  part1()
  part2()
}
