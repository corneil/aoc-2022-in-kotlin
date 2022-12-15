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


fun MatchResult.toInt(): Int {
  return this.groupValues[1].toInt()
}
fun MatchResult.toLong(): Long {
  return this.groupValues[1].toLong()
}
fun MatchResult.toList(): List<String> {
  return this.groupValues
}
fun MatchResult.string(): String {
  return this.groupValues[1]
}
