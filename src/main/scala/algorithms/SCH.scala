package algorithms

import data.Problem
import data.Types._

import scala.annotation.tailrec
import scala.util.Random
import scala.concurrent.duration._

/**
  * Created by Truji on 01/06/2016.
  */
object SCH {

  val alpha = 1
  val beta = 2
  val nAnts = 20
  val evaporationRatio = 0.1
  val elitistAnts = nAnts
  var MaxTime = 5 minutes
  val q_0 = 0.98

  val localEvap = 0.1

  def apply(problem: Problem, random: Random): (Solution, Int) = {

    val Problem(n, costes, _, greedy) = problem

    val (greedySolCost, greedySol) = greedy(random.nextInt(n))
    val baseFeromone = 1d / (n * greedySolCost)
    val initialFeromones = Vector.fill(n,n)( baseFeromone )
    val initialAnt = new SCHAnt(costes, greedySol(0), (sol: Solution, _) => {
      greedySol(sol.length-1)
    })
    initialAnt.compute


    @tailrec
    def feromoneIteration(feromonas: MatrizFeromonas, gBestAnt: SCHAnt, startTime: Long, iterations: Int): (Solution, Int) = {
      val elapsed = (System.currentTimeMillis() - startTime) millis

//      println("elapsed " + elapsed.toMinutes +":"+elapsed.toSeconds%60)
      if (elapsed >= MaxTime) (gBestAnt.path, iterations)
      else {
        val ants = (random.shuffle(1 to n-1).toVector take nAnts).map(city => {
          val ant = new SCHAnt(costes, city, transitionRule(random)(n)(costes)(feromonas))
          ant
        })

        //Actualizaciones locales
        var _feromonas = feromonas
        for (i <- 1 until n) {
          for (ant <- ants) {
            _feromonas = ant.nextMove(transitionRule(random)(n)(costes)(_feromonas), _feromonas, localEvap, baseFeromone)
          }
        }


        val bestAnt = ants minBy(ant => ant.fullCost)
        val currentBest =
          if (bestAnt.fullCost < gBestAnt.fullCost) {
            val best_aux = new SCHAnt(costes, bestAnt.start_city, (sol: Solution, _) => {
              bestAnt.path(sol.length)
            })
            best_aux.compute
            best_aux
          }
          else
            gBestAnt

//        println(currentBest.fullCost)

        val newFeromonas = updateFeromonas(_feromonas, ants, currentBest)

        feromoneIteration(newFeromonas, currentBest, startTime, iterations + 1)
      }
    }



    feromoneIteration(initialFeromones, initialAnt, System.currentTimeMillis(), 0)
  }

  def updateFeromonas(oldFeromonas: MatrizFeromonas, ants: Vector[SCHAnt], best: SCHAnt) = {
    val bcost = best.fullCost
    Vector.tabulate(oldFeromonas.length, oldFeromonas.length)((i, j) =>
      if (best.arcoVisitado(i)(j)) {
        (1-evaporationRatio)*oldFeromonas(i)(j) + evaporationRatio / bcost
      } else {
        oldFeromonas(i)(j)
      }
    )
  }

  def transitionRule(random: Random)(size: Int)(coste: MatrizCoste)(matrizFeromonas: MatrizFeromonas)(solution: Solution, city: City) = {

    val nodeValues = (0 until size) map(i => {
      if (solution.contains(i)) 0
      else
        math.pow(matrizFeromonas(city)(i), alpha) * math.pow(1d/coste(city)(i), beta)
    })

    val q = random.nextDouble()
    if (q < q_0) {
      val max = nodeValues.zipWithIndex.maxBy(item => item._1)
      max._2
    } else {
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
}
