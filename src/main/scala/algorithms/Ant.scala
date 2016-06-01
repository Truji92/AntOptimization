package algorithms

import data.Types._

class Ant(costes: MatrizCoste, val start_city: Int, transicion: Transicion) {

  private val pathBuilder = Vector.newBuilder[Int]
  private var current_city = start_city

  pathBuilder += start_city

  private val arcoVisitado: Array[Array[Boolean]] = Array.fill(costes.length, costes.length)(false)

  private def nextMove = {
    val last_city = current_city
    current_city = transicion(pathBuilder.result, current_city)

    arcoVisitado(last_city)(current_city) = true

    current_city
  }

  val path = {
    var i = 1
    while(i < costes.length) {
      pathBuilder += nextMove
      i += 1
    }
    val result = pathBuilder.result
    result
  }

  val fullCost = cost(costes, path)

  def aporte(start: Int, end: Int) = {
    if (arcoVisitado(start)(end))
      1d / fullCost
    else 0d
  }
}
