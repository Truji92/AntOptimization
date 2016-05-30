package algorithms

import data.Types._

class Ant(costes: MatrizCoste, start_city: Int, transicion: Transicion) {

  val path = Vector.newBuilder[Int]
  private var last_city = -1
  private var current_city = start_city

  private var arcosVisitados: Array[Array[Boolean]] = ???

  def nextMove = {
    last_city = current_city
    current_city = transicion(path.result, current_city)
    path += current_city

    current_city
  }

  def lastTransition = (last_city, current_city)


}
