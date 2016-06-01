package algorithms

import data.Types._

/**
  * Created by Truji on 01/06/2016.
  */
class SCHAnt(costes: MatrizCoste, val start_city: Int, transicion: Transicion) {

  private val pathBuilder = Vector.newBuilder[Int]
  private var current_city = start_city

  pathBuilder += start_city

  val arcoVisitado: Array[Array[Boolean]] = Array.fill(costes.length, costes.length)(false)

  private def nextMove = {
    val last_city = current_city
    current_city = transicion(pathBuilder.result, current_city)

    arcoVisitado(last_city)(current_city) = true

    current_city
  }

  def nextMove(transicion: Transicion, feromonas: MatrizFeromonas, evaporation: Double, feromonaInicial: Double) = {
    val last_city = current_city
    current_city = transicion(pathBuilder.result, current_city)
    arcoVisitado(last_city)(current_city) = true

    pathBuilder += current_city
    val valorF = feromonas(last_city)(current_city)
    val newF = (1-evaporation)*valorF+evaporation*feromonaInicial
    val newRow = feromonas(last_city).updated(current_city, newF)
    feromonas.updated(last_city, newRow)
  }

  def path = {
    pathBuilder.result
  }

  def fullCost = cost(costes, path)

  def compute = {
    var i = 1
    while(i < costes.length) {
      pathBuilder += nextMove
      i += 1
    }
  }
}
