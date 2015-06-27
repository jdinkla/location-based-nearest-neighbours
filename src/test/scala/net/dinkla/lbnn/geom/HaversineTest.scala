package net.dinkla.lbnn.geom

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 27.06.2015.
 */
class HaversineTest extends FunSuite {

  import Haversine._

  test("distance Hamburg <-> Emden") {
    val p1 = new Point2(53.589888, 10.003097)
    val p2 = new Point2(53.378130, 7.199429)
    val km = distanceInKm(p1.x, p1.y, p2.x, p2.y)
    assert(Math.abs(km - 186.98) < 0.01)
  }

  test("move 1 km and back") {
    val p1 = new Point2(53.589888, 10.003097)
    val (x1, y1) = moveInKm(p1.x, p1.y, 1.0, 90)
    val (x2, y2) = moveInKm(x1, y1, 1.0, 270)
    println(x2, y2)
    assert(Math.abs(x2 - p1.x) < 0.01)
    assert(Math.abs(y2 - p1.y) < 0.01)
  }
}
