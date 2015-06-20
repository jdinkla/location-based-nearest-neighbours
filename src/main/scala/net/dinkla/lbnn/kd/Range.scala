package net.dinkla.lbnn.kd

import scalaz.Ordering

/**
 * Created by dinkla on 20/06/15.
 */

trait Range {

  def inRange(p: Point2): Boolean

  def inRange(dimension: Int, value: Double): Boolean

  def inInterior(p: Point2): Boolean

  def compareIth(i: Int, m: Double): (Int, Int)

}

// p < q in both coordinates

class Rectangle(val p: Point2, val q: Point2) extends Range {

  require(p.x < q.x)
  require(p.y < q.y)

  def inRange(v: Point2): Boolean = {
    p.x <= v.x && v.x <= q.x && p.y <= v.y && v.y <= q.y
  }

  def inInterior(v: Point2): Boolean = {
    p.x < v.x && v.x < q.x && p.y < v.y && v.y < q.y
  }

  def inRange(dimension: Int, value: Double): Boolean = {
    p.ith(dimension) <= value && value <= q.ith(dimension)
  }

  def compareIth(i: Int, m: Double): (Int, Int)
    = (p.ith(i) compare m, q.ith(i) compare m)


}
