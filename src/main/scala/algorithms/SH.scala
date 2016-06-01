package algorithms

import data.Problem
import data.Types._

import scala.annotation.tailrec
import scala.collection.parallel.immutable.ParVector
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

object SH {

  val alpha = 1
  val beta = 2
  val nAnts = 20
  val evaporationRatio = 0.1
  val MaxTime = 5 minutes

  def apply(problem: Problem, random: Random): (Solution, Int) = {

    val Problem(n, costes, _, greedy) = problem

    @tailrec
    def feromoneIteration(feromonas: MatrizFeromonas, bestCost: Int, bestSolution: Solution, startTime: Long, iterations: Int): (Solution, Int) = {
      val elapsed = (System.currentTimeMillis() - startTime) millis

      println("elapsed " + elapsed.toMinutes +":"+elapsed.toSeconds%60)
      if (elapsed >= MaxTime) (bestSolution, iterations)
      else {
        val ants = (random.shuffle(1 to n-1).toVector take nAnts).map(city => {
          val ant = new Ant(costes, city, transitionRule(random)(n)(costes)(feromonas))
          ant
        })

        val bestAnt = ants minBy(ant => ant.fullCost)
        val currentBest =
          if (bestAnt.fullCost < bestCost)
            (bestAnt.fullCost, bestAnt.path)
          else
            (bestCost, bestSolution)

        println(bestAnt.fullCost)
        println(bestAnt.path.length)

        val newFeromonas = updateFeromonas(feromonas, ants)

        feromoneIteration(newFeromonas, currentBest._1, currentBest._2, startTime, iterations + 1)
      }
    }

    val (greedySolCost, greedySol) = greedy(random.nextInt(n))
    val initialFeromones = Vector.fill(n,n)( 1d / (n * greedySolCost) )

    feromoneIteration(initialFeromones, greedySolCost.toInt, greedySol, System.currentTimeMillis(), 0)
  }

  def updateFeromonas(oldFeromonas: MatrizFeromonas, ants: Vector[Ant]) =
    Vector.tabulate(oldFeromonas.length, oldFeromonas.length)((i, j) =>
      (1-evaporationRatio)*oldFeromonas(i)(j) + ants.map(_.aporte(i,j)).sum
    )

  def transitionRule(random: Random)(size: Int)(coste: MatrizCoste)(matrizFeromonas: MatrizFeromonas)(solution: Solution, city: City) = {
    val nodeValues = (0 until size) map(i => {
      if (solution.contains(i)) 0
      else
        math.pow(matrizFeromonas(city)(i), alpha) * math.pow(1d/coste(city)(i), beta)
    })

    val denom = nodeValues.sum

    val probs = nodeValues.map(_/denom).zipWithIndex
    val acumProbs = Vector.newBuilder[(Double, Int)]
    var acc = 0d

    for ((p, index) <- probs) {
      if (p != 0) {
        acc = acc + p
        acumProbs += ((acc, index))
      }
    }

    val r = random.nextDouble()

    val res = acumProbs.result.find {
      case (p, index) => p >= r
    }

    res match {
      case Some((_, i)) => i
      case None => size - 1
    }
  }

}