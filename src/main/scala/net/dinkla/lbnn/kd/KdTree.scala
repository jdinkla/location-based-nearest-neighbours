package net.dinkla.lbnn.kd

import net.dinkla.lbnn
import net.dinkla.lbnn.{Point2, Order}

/**
 * Created by dinkla on 19/06/15.
 */

/**
 * A k-dimensional tree.
 *
 * See for example ...
 */
sealed trait KdTree {

  /**
   *
   * @return the size of the KdTree
   */
  def size: Int

  /**
   *
   * @param r     a range
   * @return      the points contained in the range
   */
  def rangeQuery(r: lbnn.Range): List[Point2]

}

/**
 * Nil
 */
object Nil extends KdTree {

  val size = 0

  override def rangeQuery(r: lbnn.Range): List[Point2] = List()

  override def toString = "Nil"

}

/**
 * Leaf
 * @param value
 */
case class Leaf(val value: Point2) extends KdTree {
  val size = 1

  override def rangeQuery(r: lbnn.Range): List[Point2]
    = if (r.inRange(value)) List(value) else List()

  override def toString = s"Leaf($value)"

}

case class LeafExt[T](val extension: T, val value: Point2) extends KdTree {

  val size = 1

  override def rangeQuery(r: lbnn.Range): List[Point2]
  = if (r.inRange(value)) List(value) else List()

  override def toString = s"LeafExt(${extension.toString}, $value)"

}

/**
 *
 * @param d
 * @param med
 * @param ls
 * @param es
 * @param hs
 */
case class Node(val d: Int,
              val med: Double,
              val ls: KdTree = Nil,
              val es: KdTree = Nil,
              val hs: KdTree = Nil) extends KdTree {

  def size = 1 + ls.size + es.size + hs.size

  override def rangeQuery(r: lbnn.Range): List[Point2] = {
    r.compareIth(d, med) match {
      case (-1,  _) => hs.rangeQuery(r)
      case ( 0,  _) => es.rangeQuery(r) ++ hs.rangeQuery(r)
      case ( 1, -1) => ls.rangeQuery(r) ++ es.rangeQuery(r) ++ hs.rangeQuery(r)
      case ( 1,  0) => ls.rangeQuery(r) ++ es.rangeQuery(r)
      case ( 1,  1) => ls.rangeQuery(r)
    }
  }

  override def toString = s"Node($d, $med, $ls, $es, $hs)"

}

/**
 * KdTree companion
 */
object KdTree {

  import Order.divideByMedian2

  def build(d: Int, xs: List[Point2]): KdTree =
    xs match {
      case List() => Nil
      case List(x) => new Leaf(x)
      case _ => {
        val j: Int = (d + 1) % 2
        val p = divideByMedian2[Point2](p => p.ith(d))(xs)
        new Node(d, p.m.ith(d), build(j, p.ls), build(j, p.es), build(j, p.hs))
      }
    }

  def fromList(xs: List[Point2]): KdTree = {
    xs match {
      case List() => Nil
      case _ => build(0, xs)
    }
  }

  def buildExt[T](d: Int, xs: List[(T, Point2)]): KdTree =
    xs match {
      case List() => Nil
      case List(x) => new LeafExt[T](x._1, x._2)
      case _ => {
        val j: Int = (d + 1) % 2
        val p = divideByMedian2[(T, Point2)](p => p._2.ith(d))(xs)
        new Node(d, p.m._2.ith(d), buildExt(j, p.ls), buildExt(j, p.es), buildExt(j, p.hs))
      }
    }

  def fromListExt[T](xs: List[(T, Point2)]): KdTree = {
    xs match {
      case List() => Nil
      case _ => buildExt(0, xs)
    }
  }

}