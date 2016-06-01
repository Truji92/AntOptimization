package data

/**
  * Created by alejandro on 29/05/16.
  */
object Types {

  type MatrizCoste = Vector[Vector[Int]]
  type MatrizFeromonas = Vector[Vector[Double]]

  type CityCoord = (Double, Double)

  type City = Int

  type Solution = Vector[Int]

  type Euristica = (Int, Int) => Double

  type ReglaTransicion = MatrizCoste => MatrizFeromonas => Transicion

  type Transicion = (Solution, City) => City

}
