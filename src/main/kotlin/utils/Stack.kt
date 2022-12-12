package main.utils

class Stack<T>(private val elements: MutableList<T> = mutableListOf<T>()) {
  fun empty(): Boolean = elements.isEmpty()
  fun peek(): T = elements[elements.lastIndex]
  fun pop(): T = elements.removeAt(elements.lastIndex)
  fun push(value: T) {
    elements.add(value)
  }
  fun clear() = elements.clear()
  fun items() = elements.toList()
  fun size() = elements.size
}
