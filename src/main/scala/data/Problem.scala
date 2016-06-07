package data

import data.Types._
import util.AntUtils

import scala.collection.JavaConverters._
import scala.language.postfixOps

/**
  * Created by alejandro on 29/05/16.
  */
case class Problem(size: Int, costes: MatrizCoste, cities: Vector[CityCoord], greedy: Int => (Double, Vector[Int]))

object Problem {
  def fromFile(file: String) = {
    val util = new AntUtils(file)

    util.calculaMatrices()

    val greedy = (i: Int) => {
      val sol = util.greedy(i)
      (sol.coste.toDouble, sol.solution.asScala.toVector.map(_.toInt))
    }

    val coord1 = util.getCoord1.asScala.toVector.map(_.toDouble)
    val coord2 = util.getCoord2.asScala.toVector.map(_.toDouble)

    val matrizCostes = util.getMatrizCosto.map(_.toVector).toVector

    Problem(coord1.length, matrizCostes, coord1 zip coord2, greedy)
  }

  val ch130 = Problem.fromFile(getClass.getResource("/tsp/ch130.tsp").getPath)
  val a280 = Problem.fromFile(getClass.getResource("/tsp/a280.tsp").getPath)
  val p654 = Problem.fromFile(getClass.getResource("/tsp/p654.tsp").getPath)
//  val a280 = Problem.fromFile("../tsp/a280.tsp")
//  val p654 = Problem.fromFile("../tsp/p654")
}
