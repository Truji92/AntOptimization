package algorithms

import data.Problem
import data.Types._

import scala.util.Random

object SH {

  val alpha = 1
  val beta = 2
  val n_Ants = 20

  def apply(problem: Problem, random: Random) = {

    val Problem(n, costes, _, greedy) = problem


    def feromoneIteration(feromonas: MatrizFeromonas) = {

    }

  }

  def transitionRule(random: Random)(size: Int)(coste: MatrizCoste)(matrizFeromonas: MatrizFeromonas)(solution: Solution, city: City) = {
    val nodeValues = 0 until size map(i => {
      if (solution.contains(i)) 0
      else
        math.pow(coste(city)(i), alpha) * math.pow(matrizFeromonas(city)(i), beta)
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
