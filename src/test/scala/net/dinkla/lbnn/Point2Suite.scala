package net.dinkla.lbnn

import org.scalatest.FunSuite

/**
 * Created by dinkla on 19/06/15.
 */
class Point2Suite extends FunSuite {

  val p0 = Point2(0, 0)
  val p1 = Point2(10,12)
  val p2 = Point2(5,7)

  test("basics") {
    assert(p0.origin == p0)
    assert(p1.x == 10)
    assert(p1.y == 12)
    assert(p1._1 == 10)
    assert(p1._2 == 12)
    assert(p1.ith(0) == 10)
    assert(p1.ith(1) == 12)
    assert(p1.dimension == 2)
  }

  test("math") {
    import Point2._

    val e1 = p1 + p2
    assert(p1+p2 == Point2(p1.x+p2.x, p1.y+p2.y))
    assert(p1-p2 == Point2(p1.x-p2.x, p1.y-p2.y))
    assert(p1-p1 == p1.origin)
    assert(p1*2 == Point2(p1.x*2, p1.y*2))
    assert(2.0*p1 == Point2(p1.x*2, p1.y*2))
  }

  test("ordered") {
    assert(!(p1 < p1));
    assert(Point2(0, 0) < Point2(0, 1))
    assert(Point2(0, 0) < Point2(1, -1))
  }

}
