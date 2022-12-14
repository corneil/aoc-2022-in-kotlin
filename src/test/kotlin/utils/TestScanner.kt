package utils

import main.utils.scanInt
import main.utils.scanInts
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestScanner {
  @Test
  fun testScanInt() {
    assertEquals(2, "Monkey 2:".scanInt())
    assertEquals(213, "Monkey 213:".scanInt())
    assertEquals(312, "Monkey 312".scanInt())
    assertEquals(312, "312 monkeys".scanInt())
  }

  @Test
  fun testScanIntNegative() {
    assertEquals(-3, "Add the value -3 to the answer".scanInt())
    assertEquals(-3, "-3".scanInt())
  }

  @Test
  fun testScanInts() {
    assertEquals(listOf(5, 23, -5, 65, 1), "Items: 5, 23,-5,+65,1".scanInts())
    assertEquals(listOf(5, 23, -5, 65, 12, 1), "Items: [5, 23,-5,+65, 12,1]".scanInts())
    assertEquals(listOf(5, 23, -5, 65, 12, 1), "5,23,-5,+65,12,1".scanInts())
  }
}
