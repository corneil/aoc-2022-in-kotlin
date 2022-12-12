package utils

import kotlin.math.abs
import kotlin.math.sign

data class Coord(val x: Int, val y: Int) : Comparable<Coord> {
  fun left() = Coord(x - 1, y)
  fun right() = Coord(x + 1, y)
  fun top() = Coord(x, y - 1)
  fun bottom() = Coord(x, y + 1)
  operator fun minus(coord: Coord) = Coord(x - coord.x, y - coord.y)
  operator fun plus(coord: Coord) = Coord(x + coord.x, y + coord.y)
  fun sign(): Coord = Coord(x.sign, y.sign)
  fun surrounds() = listOf(bottom(), left(), top(), right())
  fun chebyshevDistance(target: Coord): Int = abs(target.x - x) + abs(target.y - y)
  override fun compareTo(other: Coord): Int {
    var result = x.compareTo(other.x)
    if (result == 0) {
      result = y.compareTo(other.y)
    }
    return result
  }

  override fun toString(): String {
    return "($x, $y)"
  }

}
