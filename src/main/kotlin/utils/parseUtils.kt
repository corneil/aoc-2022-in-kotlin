package main.utils

fun String.scanInt(): Int {
  var result = Long.MIN_VALUE
  var digits = false
  for(c in this) {
    if(c.isDigit()) {
      if(!digits) {
        digits = true
        result = (c - '0').toLong()
      }
    } else {
      if(digits) {
        break;
      }
    }
  }
  check(result != Long.MIN_VALUE) { "No digits found" }
  return result.toInt()
}

fun String.scanInts():List<Int> {
  val result = mutableListOf<Int>()
  var digits = false
  var current = 0
  var sign = false
  for(c in this) {
    if(c.isDigit()) {
      if(!digits) {
        digits = true
        current = c - '0'
        if(sign) {
          current *= -1
        }
      } else {
        current *= 10
        current += c - '0'
      }
    } else {
      if(digits) {
        result.add(current)
        current = 0
      }
    }
  }
  return result
}
