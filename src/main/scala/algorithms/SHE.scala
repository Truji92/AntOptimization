package algorithms

import data.Problem
import data.Types._

import scala.annotation.tailrec
import scala.language.postfixOps
import scala.util.Random
import scala.concurrent.duration._

/**
  * Created by Truji on 01/06/2016.
  */
object SHE {

  val alpha = 1
  val beta = 2
  val nAnts = 20
  val evaporationRatio = 0.1
  val elitistAnts = nAnts
  var MaxTime = 5 minutes

  def apply(problem: Problem, random: Random): (Solution, Int) = {

    val Problem(n, costes, _, greedy) = problem

    @tailrec
    def feromoneIteration(feromonas: MatrizFeromonas, gBestAnt: Ant, startTime: Long, iterations: Int): (Solution, Int) = {
      val elapsed = (System.currentTimeMillis() - startTime) millis

//      println("elapsed " + elapsed.toMinutes +":"+elapsed.toSeconds%60)
      if (elapsed >= MaxTime) (gBestAnt.path, iterations)
      else {
        val ants = (random.shuffle(1 to n-1).toVector take nAnts).map(city => {
          val ant = new Ant(costes, city, transitionRule(random)(n)(costes)(feromonas))
          ant
        })

        val bestAnt = ants minBy(ant => ant.fullCost)
        val currentBest =
          if (bestAnt.fullCost < gBestAnt.fullCost)
            new Ant(costes, bestAnt.start_city, (sol: Solution, _) => {
              bestAnt.path(sol.length)
            })
          else
            gBestAnt

//        println(currentBest.fullCost)

        val newFeromonas = updateFeromonas(feromonas, ants, currentBest)

        feromoneIteration(newFeromonas, currentBest, startTime, iterations + 1)
      }
    }

    val (greedySolCost, greedySol) = greedy(random.nextInt(n))
    val initialFeromones = Vector.fill(n,n)( 1d / (n * greedySolCost) )
    val initialAnt = new Ant(costes, greedySol(0), (sol: Solution, _) => {
      greedySol(sol.length-1)
    })

    feromoneIteration(initialFeromones, initialAnt, System.currentTimeMillis(), 0)
  }

  def updateFeromonas(oldFeromonas: MatrizFeromonas, ants: Vector[Ant], best: Ant) =
    Vector.tabulate(oldFeromonas.length, oldFeromonas.length)((i, j) =>
      (1-evaporationRatio)*oldFeromonas(i)(j) + ants.map(_.aporte(i,j)).sum + elitistAnts * best.aporte(i,j)
    )

  def transitionRule(random: Random)(size: Int)(coste: MatrizCoste)(matrizFeromonas: MatrizFeromonas)(solution: Solution, city: City) =
    SH.transitionRule(random)(size)(coste)(matrizFeromonas)(solution, city)


}
