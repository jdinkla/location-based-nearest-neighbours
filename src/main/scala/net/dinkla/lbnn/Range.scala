package net.dinkla.lbnn

/**
 * Created by dinkla on 20/06/15.
 */

/**
 * A two dimensional range.
 */
trait Range {

  /**
   * Is the point contained in the range?
   * @param p
   * @return
   */
  def inRange(p: Point2): Boolean

  /**
   * Is the ith coordinate value contained in the range?
   * @param dimension
   * @param value
   * @return
   */
  def inRange(dimension: Int, value: Double): Boolean

  /**
   * Is point p contained in the interior of the range?
   * @param p
   * @return
   */
  def inInterior(p: Point2): Boolean

  /**
   * Compare the ith coordinate value m with p and q
   * example comp i m p => LT wenn m<p
   *
   * @param i the dimension
   * @param m the value
   * @return a pair P: (C,C) where C={-1, 0, 1}
   */
  def compareIth(i: Int, m: Double): (Int, Int)

}

/**
 * A rectangular range.
 *
 * Requires: p < q in both coordinates
 *
 * @param p
 * @param q
 */
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
    = (m compare p.ith(i), m compare q.ith(i))

}
