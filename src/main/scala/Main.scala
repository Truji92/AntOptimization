import algorithms.{SHE, SH}
import data.Problem

import scala.util.Random

/**
  * Created by alejandro on 29/05/16.
  */
object Main {

  def main(args: Array[String]) {
    val Problem(size, costes, cities, greedy) = Problem.ch130

//    println("greedy: " + greedy(0))
//    println(algorithms.cost(costes, greedy(0)._2))

    val r = SHE(Problem.ch130, new Random)


    println("Sol " + r._1)
    println("iter " + r._2)
    println(algorithms.cost(costes, r._1))

  }



}
