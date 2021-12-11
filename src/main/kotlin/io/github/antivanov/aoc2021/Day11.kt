package io.github.antivanov.aoc2021

object Day11 {

  val input = """
    5483143223
    2745854711
    5264556173
    6141336146
    6357385478
    4167524645
    2176841721
    6882881134
    4846848554
    5283751526
  """.trimIndent()

  data class Point(val x: Int, val y: Int)

  data class Grid(val locations: Array<Array<Int>>) {
    val width = locations[0].size
    val height = locations.size

    private fun liesInGrid(p: Point): Boolean =
      p.x >= 0 && p.x < width && p.y >= 0 && p.y < height

    private fun getAdjacentPoints(p: Point): List<Point> {
      val subGrid = (-1..1).flatMap { xOffset ->
        (-1..1).map { yOffset ->
          p.copy(x = p.x + xOffset, y = p.y + yOffset)
        }
      }
      return subGrid.filter {
        it != p && liesInGrid(it)
      }
    }

    fun updateGrid(): Grid {
      incrementLevels(locations)
      checkIfToFlashAndPropagateIfFlashed(locations)
      return Grid(locations)
    }

    fun flashesCount(): Int =
      (0 until width).flatMap { x ->
        (0 until height).filter { y ->
          locations[y][x] == 0
        }
      }.count()

    private fun incrementLevels(locations: Array<Array<Int>>): Array<Array<Int>> {
      (0 until width).flatMap { x ->
        (0 until height).map { y ->
          locations[y][x] += 1
        }
      }
      return locations
    }

    private fun checkIfToFlashAndPropagateIfFlashed(locations: Array<Array<Int>>): Unit {
      (0 until width).flatMap { x ->
        (0 until height).map { y ->
          checkIfToFlashAndPropagateIfFlashed(Point(x, y), locations)
        }
      }
    }

    private fun checkIfToFlashAndPropagateIfFlashed(p: Point, locations: Array<Array<Int>>): Unit {
      if (locations[p.y][p.x] > 9) {
        locations[p.y][p.x] = 0
        propagateLevelChangesFrom(p, locations)
      }
    }

    private fun propagateLevelChangesFrom(p: Point, locations: Array<Array<Int>>): Unit {
      getAdjacentPoints(p).forEach {
        if (locations[it.y][it.x] != 0) {
          locations[it.y][it.x] += 1
          checkIfToFlashAndPropagateIfFlashed(it, locations)
        }
      }
    }

    private fun resetAfterFlash(locations: Array<Array<Int>>): Unit {
      (0 until width).flatMap { x ->
        (0 until height).map { y ->
          if (locations[y][x] > 9) {
            locations[y][x] += 0
          }
        }
      }
    }

    override fun toString(): String =
      locations.map {
        it.joinToString("")
      }.joinToString("\n")
  }

  fun parseInput(input: String): Array<Array<Int>> =
    input.split("\n").map {
      it.trim()
    }.map {
      it.toList().map { digit ->
        digit.toString().toInt()
      }.toTypedArray()
    }.toTypedArray()

  fun part1(parsedInput: Array<Array<Int>>): Int {
    val clonedParsedInput = parsedInput.copyOf().map { it.copyOf() }.toTypedArray()
    val initialGrid = Grid(clonedParsedInput)
    val totalSteps = 100
    val (_, finalFlashes) = (1..totalSteps).fold(initialGrid to 0) { (grid, flashes), _ ->
      val updatedGrid = grid.updateGrid()
      val updatedFlashes = flashes + updatedGrid.flashesCount()
      updatedGrid to updatedFlashes
    }
    return finalFlashes
  }
}

fun main() {
  val parsedInput = Day11.parseInput(Day11.input)
  println(Day11.part1(parsedInput))
}