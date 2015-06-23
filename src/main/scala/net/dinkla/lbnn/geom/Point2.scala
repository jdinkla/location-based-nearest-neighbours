package net.dinkla.lbnn.geom

/**
 * Created by Dinkla on 23.06.2015.
 */
object Point2 {

  import scala.language.implicitConversions

  def apply(x: Double = 0, y: Double = 0) = new Point2(x, y)
  def apply(xy: (Double, Double)) = new Point2(xy._1, xy._1)

  //implicit def intToPoint2(v: Int) = Point2(v)
  implicit def doubleToPoint2(v: Double) = new Point2Temporary(v)

}

class Point2(val x: Double, val y: Double)
  extends Point[Double] with Ordered[Point2] {

  val dimension = 2

  def ith(i: Int) = if (i==0) x else y

//  type P = Point2[T]
  lazy val origin = new Point2(0, 0)

  def _1 = x
  def _2 = y

  def +(p: Point2): Point2 = Point2(x+p.x, y+p.y)
  def -(p: Point2): Point2 = Point2(x-p.x, y-p.y)
  def *(d: Double): Point2 = Point2(x * d, y * d)
  def **(p: Point2): Double = x * p.x + y * p.y

  def sqrNorm(p: Point2): Double = {
    val r = (this - p)
    r ** r
  }

  def norm(p: Point2): Double = Math.sqrt(sqrNorm(p))

  override def equals(other: Any): Boolean = {
    other match {
      //case that: Point2[T] => (0 until dimension).forall(i => ith(i) == that.ith(i))
      case that: Point2 => x == that.x && y == that.y
      case _ => false
    }
  }

  override def hashCode(): Int = (x,y).hashCode()

  override def compare(that: Point2): Int = {
    val i1 = x compare that.x
    if (i1 != 0) i1
    else y compare that.y
  }

  override def toString: String = s"($x, $y)"
}