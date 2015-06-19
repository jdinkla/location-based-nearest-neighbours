package net.dinkla.lbnn.kd

/**
 * Created by dinkla on 19/06/15.
 */

trait Point[T] {
  def dimension(): Int
  def ith(i: Int): T
  //type P
  //val origin: P
}

class Point2(val x: Double, val y: Double)
  extends Point[Double] with Ordered[Point2] {

  val dimension = 2

  def ith(i: Int) = if (i==0) x else y

//  type P = Point2[T]
  lazy val origin = new Point2(0, 0)

  def _1 = x
  def _2 = y

  def +(other: Point2): Point2 = Point2(x+other.x, y+other.y)
  def -(other: Point2): Point2 = Point2(x-other.x, y-other.y)
  def *(d: Double): Point2 = Point2(x * d, y * d)

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

}

final class X2(val s: Double) extends Point2(0, 0) {

  def *(p: Point2): Point2 = p * s

}

object Point2 {

  def apply(x: Double = 0, y: Double = 0) = new Point2(x, y)

  //implicit def intToPoint2(v: Int) = Point2(v)
  implicit def doubleToPoint2(v: Double) = new X2(v)

}

