package io.github.antivanov.aoc2021

object Day3 {
  val input = """
00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010
""".trimIndent()

  private fun toDecimalFromBits(bits: List<Int>): Int =
    bits.joinToString(separator = "").toInt(2)

  private fun computeOneBitFrequencies(measurements: List<List<Int>>): List<Int> {
    val measurementWidth = measurements[0].size
    return (0 until measurementWidth).map { bitIndex ->
      measurements.fold(0) { current, measurement ->
        current + measurement[bitIndex]
      }
    }
  }

  private fun frequenciesToMeasurement(oneBitFrequencies: List<Int>, measurementsSize: Int, bitSetter: (Int, Int) -> Int): Int {
    val gammaBits = oneBitFrequencies.map {
      bitSetter(it, measurementsSize - it)
    }
    return toDecimalFromBits(gammaBits)
  }

  private fun computeGamma(oneBitFrequencies: List<Int>, measurementsSize: Int): Int {
    return frequenciesToMeasurement(oneBitFrequencies, measurementsSize) { onesCount, zerosCount ->
      if (onesCount > zerosCount) 1 else 0
    }
  }

  private fun computeEpsilon(oneBitFrequencies: List<Int>, measurementsSize: Int): Int {
    return frequenciesToMeasurement(oneBitFrequencies, measurementsSize) { onesCount, zerosCount ->
      if (onesCount <= zerosCount) 1 else 0 // 0-biased selection, should be 1-biased instead for the "oxygen generator rating" - !
    }
  }

  fun part1(measurements: List<List<Int>>): Int {
    val oneBitFrequencies = computeOneBitFrequencies(measurements)
    val gamma = computeGamma(oneBitFrequencies, measurements.size)
    val epsilon = computeEpsilon(oneBitFrequencies, measurements.size)
    return gamma * epsilon
  }

  private fun oxygenGeneratorRatingBitChooser(onesCount: Int, zerosCount: Int): Int {
    return if (onesCount >= zerosCount) 1 else 0
  }

  private fun co2ScrubberRatingBitChooser(onesCount: Int, zerosCount: Int): Int {
    return if (onesCount < zerosCount) 1 else 0
  }

  private fun computeRating(measurements: List<List<Int>>, currentBitIndex: Int, bitChooser: (Int, Int) -> Int): Int {
    if (currentBitIndex >= measurements[0].size && measurements.size > 1) {
      throw IllegalStateException("Measurements $measurements are still left while all bits were considered")
    }
    val onesCount = measurements.fold(0) { onesCount, measurement ->
      onesCount + measurement[currentBitIndex]
    }
    val zerosCount = measurements.size - onesCount
    val bitToLookFor = bitChooser(onesCount, zerosCount)
    val filteredMeasurements = measurements.filter { it[currentBitIndex] == bitToLookFor }
    if (filteredMeasurements.size > 1) {
      return computeRating(filteredMeasurements, currentBitIndex + 1, bitChooser)
    } else {
      val foundMeasurement = filteredMeasurements[0]
      return toDecimalFromBits(foundMeasurement)
    }
  }

  private fun oxygenGeneratorRating(measurements: List<List<Int>>): Int {
    return computeRating(measurements, 0, ::oxygenGeneratorRatingBitChooser)
  }

  private fun co2ScrubberRating(measurements: List<List<Int>>): Int {
    return computeRating(measurements, 0, ::co2ScrubberRatingBitChooser)
  }

  fun part2(measurements: List<List<Int>>): Int {
    val oxygenGeneratorMeasurement = oxygenGeneratorRating(measurements)
    val co2ScrubberMeasurement = co2ScrubberRating(measurements)
    return oxygenGeneratorMeasurement * co2ScrubberMeasurement
  }
}

fun main() {
  val numbers: List<String> = Day3.input.split("\n")
  val measurements: List<List<Int>> = numbers.map { it.toCharArray().toList().map { it.toString().toInt() } }
  println(Day3.part1(measurements))
  println(Day3.part2(measurements))
}