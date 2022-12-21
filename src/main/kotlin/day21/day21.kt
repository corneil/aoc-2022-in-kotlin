package day21

import day21.Day21.eval
import main.utils.measureAndPrint
import space.kscience.kmath.functions.ListRationalFunction
import space.kscience.kmath.functions.ListRationalFunctionSpace
import space.kscience.kmath.functions.listRationalFunctionSpace
import space.kscience.kmath.operations.JBigIntegerField
import space.kscience.kmath.operations.Ring
import utils.checkNumber
import utils.readFile
import utils.readLines
import utils.separator
import java.math.BigInteger

enum class Operation(val c: Char) {
  PLUS('+'),
  MINUS('-'),
  MULTIPLY('*'),
  DIVIDE('/')
}

sealed interface BaseMonkey {
  val name: String
}

data class ValueMonkey(
  override val name: String,
  val value: Long
) : BaseMonkey

data class OperationMonkey(
  override val name: String,
  val operation: Operation,
  val left: String,
  val right: String
) : BaseMonkey

object Day21 {
  // create a lif od rational functions for the polynomial solver.
  context(ListRationalFunctionSpace<C, Ring<C>>)
  fun <C> BaseMonkey.eval(
    monkeys: Map<String, BaseMonkey>,
    function: (Long) -> C
  ): ListRationalFunction<C> =
    if (name == "humn") {
      ListRationalFunction(listOf(ring.zero, ring.one))
    } else when (this) {
      is ValueMonkey -> ListRationalFunction(listOf(function(value)))
      is OperationMonkey -> {
        val leftValue = monkeys[left]?.eval(monkeys, function) ?: error("Cannot find $left")
        val rightValue = monkeys[right]?.eval(monkeys, function) ?: error("Cannot find $right")
        when (operation) {
          Operation.PLUS -> leftValue + rightValue
          Operation.MINUS -> leftValue - rightValue
          Operation.MULTIPLY -> leftValue * rightValue
          Operation.DIVIDE -> leftValue / rightValue
        }
      }
    }
}

fun main() {

  val testLines = readLines(
    """root: pppw + sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 5
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32"""
  )
  val lines = readFile("day21")

  class Monkey(val name: String, val expression: String, var shout: (() -> Long)? = null) {
    fun answer(): Long {
      return shout?.invoke() ?: error("Monkey $name cannot shout with $expression")
    }
  }

  fun calcSolution1(input: List<String>): Long {
    val monkeys = input.map { line ->
      line.split(": ")
        .let { (a, b) ->
          Monkey(a.trim(), b.trim()) }
    }.associateBy { it.name }
    monkeys.values.forEach { monkey ->
      val value = monkey.expression.toLongOrNull()
      if (value != null) {
        monkey.shout = { -> value }
      } else {
        val (a, op, b) = monkey.expression.split(" ")
        val left = monkeys[a] ?: error("Cannot find Monkey: $a")
        val right = monkeys[b] ?: error("Cannot find Monkey: $b")
        monkey.shout = when (op) {
          "+" -> { -> left.answer() + right.answer() }
          "/" -> { -> left.answer() / right.answer() }
          "-" -> { -> left.answer() - right.answer() }
          "*" -> { -> left.answer() * right.answer() }
          else -> error("Cannot evaluate $op in ${monkey.expression}")
        }
      }
    }
    val root = monkeys["root"] ?: error("Cannot find root")
    return root.answer()
  }

  fun calcSolution2(input: List<String>): Long {
    val monkeyRules = input.associate { line ->
      line.split(": ")
        .let { (a, b) ->
          a.trim() to b.trim()
        }
    }.toMutableMap()

    val monkeys = monkeyRules.map { (name, expression) ->
      val value = expression.toLongOrNull()
      name to
        if (value != null) {
          ValueMonkey(name, value)
        } else {
          val (left, op, right) = expression.split(" ")
          val operation = Operation.values().find { it.c == op.trim().first() }
          check(operation != null) { "Operation not found $op" }
          OperationMonkey(name, operation, left, right)
        }
    }.toMap()

    val root = monkeys["root"] ?: error("Cannot find root")

    @Suppress("UNCHECKED_CAST")
    val answer = with(
      JBigIntegerField.listRationalFunctionSpace as
        ListRationalFunctionSpace<BigInteger, Ring<BigInteger>>
    ) {
      root as OperationMonkey
      val leftMonkey = monkeys[root.left] ?: error("Cannot find ${root.left}")
      val rightMonkey = monkeys[root.right] ?: error("Cannot find ${root.right}")
      val left = leftMonkey.eval(monkeys, BigInteger::valueOf)
      val right = rightMonkey.eval(monkeys, BigInteger::valueOf)
      val result = left.numerator * right.denominator - right.numerator * left.denominator
      when (result.degree) {
        0 -> error("We're done")
        1 -> -result.coefficients[0] / result.coefficients[1]
        else -> error("Wow degree ${result.degree}")
      }
    }.toLong()
    return answer
  }

  fun part1() {
    // warmup
    repeat(1000) { calcSolution1(testLines) }

    val testResult = measureAndPrint("Part 1 Test Time: ") { calcSolution1(testLines) }
    println("Part 1 Test Answer = $testResult")
    checkNumber(testResult, 152)

    val result = measureAndPrint("Part 1 Time: ") { calcSolution1(lines) }
    println("Part 1 Answer = $result")
    checkNumber(result, 194501589693264L)
  }

  fun part2() {
    // warmup
    repeat(1000) { calcSolution2(testLines) }

    val testResult = measureAndPrint("Part 2 Test Time: ") { calcSolution2(testLines) }
    println("Part 2 Test Answer = $testResult")
    checkNumber(testResult, 301)

    val result = measureAndPrint("Part 2 Time: ") { calcSolution2(lines) }
    println("Part 2 Answer = $result")
    checkNumber(result, 3887609741189L)
  }
  println("Day - 21")
  part1()
  separator()
  part2()
}
