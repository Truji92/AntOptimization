import data.Types.{Solution, MatrizCoste}

package object algorithms {

  def cost(costes: MatrizCoste, solution: Solution) = {
    var acc = 0
    for(i <- 0 until solution.length - 1 ) {
      val j = solution(i)
      val k = solution(i+1)
      acc = acc + costes(j)(k)
    }
  }
}
