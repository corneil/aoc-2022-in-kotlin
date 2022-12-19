package utils

inline fun <T> checkEquals(a: T, b: T) {
  check(a == b) { "Expected $a not $b " }
}

inline fun <reified T: Number> checkNumber(a: T, b: T) {
  check(a == b) {
    val diff = a.toLong() - b.toLong()
    "Expected $a not $b diff $diff"
  }
}

inline fun <T: Collection<T>> checkCollection(a: T, b: T) {
  check(a == b) { "Expected $a not $b diff ${a - b}" }
}
