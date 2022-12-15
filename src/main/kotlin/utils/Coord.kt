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
  fun surroundsDiag() = listOf(
    bottom(),
    bottom().left(),
    left(),
    left().top(),
    top(),
    top().right(),
    right(),
    right().bottom()
  )

  fun chebyshevDistance(target: Coord): Int = abs(x - target.x) + abs(y - target.y)
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
