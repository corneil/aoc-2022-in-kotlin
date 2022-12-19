package main.utils

import java.util.*

data class Edge<T>(val c1: T, val c2: T, val distance: Int = 1)

/**
 * This Graph is stateful and cannot be shared.
 * Maintain the collections of edges and use when needed.
 */

class Graph<T : Comparable<T>>(edges: Collection<Edge<T>>, directed: Boolean) {
  class Vertex<T : Comparable<T>>(
    val key: T,
    var distance: Int = Int.MAX_VALUE,
    var prev: Vertex<T>? = null,
    val neighbours: MutableMap<Vertex<T>, Int> = mutableMapOf()
  ) : Comparable<Vertex<T>> {
    override fun compareTo(other: Vertex<T>): Int {
      var result = distance.compareTo(other.distance)
      if (result == 0) {
        result = key.compareTo(other.key)
      }
      return result
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Vertex<*>

      if (key != other.key) return false
      if (distance != other.distance) return false

      return true
    }

    override fun hashCode(): Int {
      var result = key.hashCode()
      result = 31 * result + distance
      return result
    }

    fun findPath(path: MutableList<Pair<T, Int>>) {
      if (this == prev) {
        path.add(Pair(key, distance))
      } else if (prev != null) {
        prev!!.findPath(path)
        path.add(Pair(key, distance))
      }
    }

    fun findNeighbours(path: MutableList<Pair<T, Int>>, depth: Int, matcher: (T) -> Boolean): Boolean {
      return if (depth >= 0) {
        val valid = neighbours.filter {
          matcher(it.key.key)
        }.filter {
          it.key.findNeighbours(path, depth - 1, matcher)
        }.map { it.key.key to it.value + depth }
        path.addAll(valid)
        true
      } else
        false
    }
  }

  private val graph = HashMap<T, Vertex<T>>(edges.size)

  init {
    for (e in edges) {
      if (!graph.containsKey(e.c1)) {
        graph[e.c1] = Vertex(e.c1)
      }
      if (!graph.containsKey(e.c2)) {
        graph[e.c2] = Vertex(e.c2)
      }
    }

    // another pass to set neighbouring vertices
    for (e in edges) {
      graph[e.c1]!!.neighbours[graph[e.c2]!!] = e.distance
      if (!directed) {
        graph[e.c2]!!.neighbours[graph[e.c1]!!] = e.distance
      }
    }
  }

  /**
   * Set the starting point for a findPath
   */
  fun dijkstra(start: T) {
    if (!graph.containsKey(start)) {
      error("Cannot find start $start")
    }
    val source = graph[start]
    val q = TreeSet<Vertex<T>>()

    // set-up vertices
    for (v in graph.values) {
      v.prev = if (v == source) source else null
      v.distance = if (v == source) 0 else Int.MAX_VALUE
      q.add(v)
    }

    dijkstra(q)
  }

  private fun dijkstra(q: TreeSet<Vertex<T>>) {
    while (!q.isEmpty()) {

      val u = q.pollFirst()
      if (u.distance == Int.MAX_VALUE) break
      for (a in u.neighbours) {
        val v = a.key

        val alternateDist = u.distance + a.value
        if (alternateDist < v.distance) {
          q.remove(v)
          v.distance = alternateDist
          v.prev = u
          q.add(v)
        }
      }
    }
  }

  fun findNeighbours(start: T, depth: Int, matches: (T) -> Boolean): Set<Pair<T, Int>> {
    dijkstra(start)
    val path = mutableListOf<Pair<T, Int>>()
    graph[start]!!.findNeighbours(path, depth, matches)
    return path.toSet()
  }

  /**
   * Set starting point and determine the shortest path
   */
  fun findPath(start: T, end: T): List<Pair<T, Int>> {
    dijkstra(start)
    return findPath(end)
  }

  /**
   * Find the shortest path from start to end. That last pair.second will also be to total distance
   */
  fun findPath(end: T): List<Pair<T, Int>> {
    if (!graph.containsKey(end)) {
      error("Cannot find $end")
    }
    val path = mutableListOf<Pair<T, Int>>()
    graph[end]!!.findPath(path)
    return path.toList()
  }

  fun findNeighbours(end: T, comparator: (left: T, right: T) -> Int): List<T> {
    val path = mutableListOf<Pair<T, Int>>()
    graph[end]!!.findNeighbours(path, 1) { true }
    return path.sortedWith { o1, o2 ->
      comparator(o1.first, o2.first)
    }.map { it.first }
  }
}
