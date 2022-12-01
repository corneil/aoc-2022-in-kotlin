package io.github.corneil.aoc_2022_in_kotlin.utils

import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

