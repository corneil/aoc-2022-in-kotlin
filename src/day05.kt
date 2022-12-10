package day05
import utils.*
data class Instruction(val count: Int, val from: Int, val to: Int)

fun main() {

  fun readStack(lines: List<String>, stack: Int): Stack<Char> {
    val result = mutableListOf<Char>()
    val offset = stack * 4 + 1
    for (line in lines) {
      if (line.length > offset) {
        val crate = line[offset]
        if (crate in 'A'..'Z') {
          result.add(crate)
        }
      }
    }
    result.reverse()
    println("Stack[${stack + 1}]=${result.joinToString("")}")
    return Stack(result)
  }

  fun loadStacks(input: List<String>): List<Stack<Char>> {
    val lines = input.toMutableList()
    val numbers = lines.last().split(" ").mapNotNull { if(it.isNotBlank()) it.toInt() else null }
    lines.removeAt(lines.lastIndex)
    val stacks = mutableListOf<Stack<Char>>()
    for (stack in numbers.indices) {
      stacks.add(readStack(lines, stack))
    }
    return stacks.toList()
  }

  fun loadInstructions(input: List<String>): List<Instruction> {
    val regex = """move (\d+) from (\d+) to (\d+)""".toRegex()
    return input.mapNotNull { line ->
      regex.find(line)?.let {
        val (count, from, to) = it.destructured
        Instruction(count.toInt(), from.toInt(), to.toInt())
      }
    }
  }

  fun perform(stacks: List<Stack<Char>>, instruction: Instruction) {
    for (i in 1..instruction.count) {
      val crate = stacks[instruction.from - 1].pop()
      stacks[instruction.to - 1].push(crate)
    }
  }

  fun perform9001(stacks: List<Stack<Char>>, instruction: Instruction) {
    val tmp = Stack<Char>()
    for (i in 1..instruction.count) {
      val crate = stacks[instruction.from - 1].pop()
      tmp.push(crate)
    }
    while (!tmp.empty()) {
      stacks[instruction.to - 1].push(tmp.pop())
    }
  }

  fun performAndFindTops(stacks: List<Stack<Char>>, instructions: List<Instruction>): String {
    instructions.forEach {
      perform(stacks, it)
    }
    stacks.forEach { stack -> println("[${stack.items().joinToString("")}]") }
    return stacks.joinToString("") { it.peek().toString() }
  }

  fun performAndFindTops9001(stacks: List<Stack<Char>>, instructions: List<Instruction>): String {
    instructions.forEach {
      perform9001(stacks, it)
    }
    stacks.forEach { stack -> println("[${stack.items().joinToString("")}]") }
    return stacks.joinToString("") { it.peek().toString() }
  }

  val test = """    [D]
[N] [C]
[Z] [M] [P]
 1   2   3

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
"""

  val testInputs = readLinesGroup(test)
  val input = readFileGroup("day05")
  fun part1() {
    val testTops = performAndFindTops(loadStacks(testInputs[0]), loadInstructions(testInputs[1]))
    println("Part 1 Test Tops = $testTops")
    check(testTops == "CMZ")
    val tops = performAndFindTops(loadStacks(input[0]), loadInstructions(input[1]))
    println("Part 1 Tops = $tops")
    check(tops == "PTWLTDSJV")
  }

  fun part2() {
    val testTops = performAndFindTops9001(loadStacks(testInputs[0]), loadInstructions(testInputs[1]))
    println("Part 2 Test Tops = $testTops")
    check(testTops == "MCD")
    val tops = performAndFindTops9001(loadStacks(input[0]), loadInstructions(input[1]))
    println("Part 2 Tops = $tops")
    check(tops == "WZMFVGGZP")
  }
  println("Day - 05")
  part1()
  part2()

}
