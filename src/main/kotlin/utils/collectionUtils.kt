package main.utils

inline fun <reified T> Collection<T>.filterUnique(): Collection<T> {
  val visiting = mutableSetOf<T>()
  return this.filter {
    if (visiting.contains(it)) {
      false
    } else {
      visiting.add(it)
      true
    }
  }
}
