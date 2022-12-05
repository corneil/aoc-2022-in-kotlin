import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun readFile(name: String) = File("src", "$name.txt")
    .readLines()

fun readText(text:String) = text.split("\n").map { it.trimEnd() }
