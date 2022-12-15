package utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class TestUnion {
  @Test
  fun testExcluded() {
    listOf(-2..2, 2..2, 2..14, 12..12, 14..18, 16..24).sortedBy { it.first }.let { ranges ->
      val excluded = ranges.flatMap { it.exclude(8) }
      println("Excluded=$excluded")
      assertEquals(setOf(-2..2, 2..2, 2..7, 9..14, 12..12, 14..18, 16..24), excluded.toSet())
    }
  }

  @Test
  fun testRangeIntersection() {
    listOf(1..3, 2..10, 14..17).sortedBy { it.first }.let { ranges ->
      val combined = joinRanges(ranges).toSet()
      println(combined)
      assertEquals(setOf(1..10, 14..17), combined)
    }
    listOf(-2..2, 2..2, 2..14, 12..12, 14..18, 16..24).sortedBy { it.first }.let { ranges ->
      val combined = joinRanges(ranges).toSet()
      println(combined)
      assertEquals(setOf(-2..24), combined)
    }
    listOf(-2..2, 2..2, 2..14, 12..12, 14..18, 16..24).sortedBy { it.first }.let { ranges ->
      val excluded = ranges.flatMap { it.exclude(8) }
      println("Excluded=$excluded")
      val combined = joinRanges(excluded).toSet()
      println("Combined=$combined")
      assertEquals(setOf(-2..7, 9..24), combined)
    }
  }

  @Test
  fun testRangeExclude() {
    (1..10).exclude(3).let {
      println(it)
      assertEquals(listOf(1..2, 4..10), it)
    }
    (1..10).exclude(1).let {
      println(it)
      assertEquals(listOf(2..10), it)
    }

    (1..10).exclude(11).let {
      println(it)
      assertEquals(listOf(1..10), it)
    }
    (2..2).exclude(2).let {
      println(it)
      assertEquals(emptyList(), it)
    }
  }
}
