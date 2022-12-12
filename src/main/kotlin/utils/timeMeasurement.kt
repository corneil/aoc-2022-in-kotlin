package main.utils

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.system.measureNanoTime

private val dividers = listOf(
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
