// using scala 3.0.2
// using scala-js

package day3

import scala.scalajs.js
import scala.scalajs.js.annotation._

@main def part1(): Unit =
  val input = readInput()
  val answer = part1(input)
  println(s"The solution is $answer")

@main def part2(): Unit =
  val input = readInput()
  val answer = part2(input)
  println(s"The solution is $answer")

def readInput(): String =
  NodeFS.readFileSync("input/day3", "utf-8")

object NodeFS:
  @js.native @JSImport("fs", "readFileSync")
  def readFileSync(path: String, charset: String): String = js.native

def part1(input: String): Int =
  val bitLines = input.linesIterator.map(parseBitLine).toList

  val sumsOfOneBits = bitLines.reduceLeft((prevSum, line) =>
    for ((prevBitSum, lineBit) <- prevSum.zip(line))
      yield prevBitSum + lineBit
  )
  val total = bitLines.size // this will walk the list a second time, but that's OK

  val gammaRateBits =
    for (sumOfOneBits <- sumsOfOneBits)
      yield (if (sumOfOneBits * 2 > total) 1 else 0)
  val gammaRate = bitLineToInt(gammaRateBits)

  val epsilonRateBits =
    for (sumOfOneBits <- sumsOfOneBits)
      yield (if (sumOfOneBits * 2 < total) 1 else 0)
  val epsilonRate = bitLineToInt(epsilonRateBits)

  gammaRate * epsilonRate

type BitLine = IndexedSeq[Int]

def parseBitLine(line: String): BitLine =
  line.map(c => c - '0') // 1 or 0

def bitLineToInt(bitLine: BitLine): Int =
  Integer.parseInt(bitLine.mkString, 2)

def part2(input: String): Int =
  val bitLines = input.linesIterator.map(parseBitLine).toList

  val oxygenGeneratorRatingLine = recursiveFilter(bitLines, 0,
      (totalOnes, total) => if (totalOnes * 2 >= total) 1 else 0)
  val oxygenGeneratorRating = bitLineToInt(oxygenGeneratorRatingLine)

  val co2ScrubberRatingLine = recursiveFilter(bitLines, 0,
      (totalOnes, total) => if (totalOnes * 2 < total) 1 else 0)
  val co2ScrubberRating = bitLineToInt(co2ScrubberRatingLine)

  oxygenGeneratorRating * co2ScrubberRating

@scala.annotation.tailrec
def recursiveFilter(bitLines: List[BitLine], bitPosition: Int,
    bitCriteria: (Int, Int) => Int): BitLine =
  bitLines match
    case Nil =>
      throw new AssertionError("this shouldn't have happened")
    case onlyLine :: Nil =>
      onlyLine
    case _ =>
      val totalOnes = bitLines.count(line => line(bitPosition) == 1)
      val total = bitLines.size
      val bitToKeep = bitCriteria(totalOnes, total)
      val filtered = bitLines.filter(line => line(bitPosition) == bitToKeep)
      recursiveFilter(filtered, bitPosition + 1, bitCriteria)
