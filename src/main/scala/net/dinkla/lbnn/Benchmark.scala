package net.dinkla.lbnn

import net.dinkla.lbnn.kd.KdTree

import scala.util.Random
import net.dinkla.lbnn.kd.KdTree._
import Timer._

/**
 * Created by dinkla on 20/06/15.
 */
object Benchmark {

  def randomDouble = (Random.nextDouble() - 0.5) * 180
  def randomPoint: Point2 = Point2(randomDouble, randomDouble)

  def randomPoints: Stream[Point2] = {
    randomPoint #:: randomPoints
  }

  def b1(n: Int = 10, steps: Int = 10, size: Double = 10.0) {
    val rs: List[Point2] = randomPoints.take(n).toList
//    println(rs)

    var kdt: KdTree = null

    timerGc("creation") {
      kdt = KdTree.fromList(rs)
    }

    for (i <- 1 to steps) {
      val p1 = randomPoint
      val p2 = p1 + Point2(size, size)
      val r = new Rectangle(p1, p2)
      timerGc(s"rangeQuery $i") {
        val ps = kdt.rangeQuery(r)
        println(s"$i ${ps.size}")
      }
    }
  }

  def main (args: Array[String]) {
    b1(1000 * 1000, 10, 10)
  }

}
