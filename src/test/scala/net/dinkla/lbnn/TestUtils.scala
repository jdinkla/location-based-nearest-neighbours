package net.dinkla.lbnn

/**
 * Utilites for the tests.
 *
 * Created by dinkla on 20/06/15.
 */
object TestUtils {

  type Ls = List[(Double, Double)]

  val ls1 : List[Double] = List(1, 3, 5, 7, 9)
  val ps1 = ls1.map { x => Point2(x, (x - 5) * (x - 5)) }

  val ls2 : Ls = List((1.0, 1.0), (4.5, 2.0), (3.5, 4.0), (1.5, 4.5),
    (2.5, 2.0), (3.0, 3.0), (5.0, 3.5), (0.5, 3.0), (4.0, 1.0), (1.5, 2.5))
  val ps2 = ls2.map { x => Point2(x._1, x._2) }

}
