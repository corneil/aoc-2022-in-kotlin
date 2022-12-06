import java.io.File

/**
 * Reads lines from the given input txt file.
 * @param name The name of the file to read
 */
fun readFile(name: String) = File("src", "$name.txt").readLines()

/**
 * Reads whole file into a text string
 * @param name The name of the file to read
 */
fun readFileToString(name: String) = File("src", "$name.txt").readText()

/**
 * Read file into lines and group after condition is met place following lines into new list
 * @param name The name of the file to read
 * @param condition A lambda to evaluate to determine the grouping condition. The line will be excluded from the output. The default will be a blank line
 */
fun readFileGroup(name: String, condition: (String) -> Boolean = { it.isBlank() }): List<List<String>> {
  val input = File("src", "$name.txt").readLines()
  val result = mutableListOf<List<String>>()
  val list = mutableListOf<String>()
  input.forEach {
    if (condition(it)) {
      result.add(list.toList())
      list.clear()
    } else {
      list.add(it)
    }
  }
  if (list.isNotEmpty()) {
    result.add(list.toList())
  }
  return result.toList()
}

/**
 * Parses input text as lines and removes trailing whitespace like \r
 * @param text This is typically text that has been declared between """ or has embedded \n
 */
fun readLines(text: String) = text.split("\n").map { it.trimEnd() }
