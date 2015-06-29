package net.dinkla.lbnn.geom

/**
 * A rectangular range.
 *
 * Requires: p < q in both coordinates
 *
 * @param p
 * @param q
 */
class Rectangle(val p: Point2, val q: Point2)
  extends Range
  with Serializable {

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
