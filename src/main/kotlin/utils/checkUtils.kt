package utils

inline fun <T> checkEquals(a: T, b: T) {
  check(a == b) { "Expected $b not $a" }
}

inline fun <reified T: Number> checkNumber(a: T, b: T) {
  check(a == b) {
    val diff = a.toLong() - b.toLong()
    "Expected $b not $a diff $diff"
  }
}

inline fun <T: Collection<T>> checkCollection(a: T, b: T) {
  check(a == b) { "Expected $b not $a diff ${a - b}" }
}
