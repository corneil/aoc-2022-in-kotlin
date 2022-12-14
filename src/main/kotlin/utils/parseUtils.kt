package main.utils

val singleNumber = """\.*([+|-]*\d+)\D*""".toRegex()
fun String.scanNumber(): String? {
  val result = singleNumber.find(this)
  return result?.let {
    val (value) = it.destructured
    value
  }
}

fun String.scanNumbers(): List<String> {
  val result = mutableListOf<String>()
  var index = 0
  do {
    val match = singleNumber.find(this, index)
    if (match != null) {
      val (value) = match.destructured
      result.add(value)
      index = match.range.last
    }
  } while (index < this.lastIndex)
  return result.toList()
}

fun String.scanInt(): Int {
  val value = this.scanNumber()
  return value?.toInt() ?: error("No digits found")
}

fun String.scanInts(): List<Int> {
  return this.scanNumbers().map { it.toInt() }
}
