package net.dinkla.lbnn.kd

import net.dinkla.lbnn
import net.dinkla.lbnn.geom.{Point2, Range}
import net.dinkla.lbnn.utils.Order

import scala.collection.mutable.ListBuffer

/**
 * Created by dinkla on 19/06/15.
 */

/**
 * A k-dimensional tree.
 *
 * See for example ...
 */
sealed trait KdTree[+T] {

  /**
   *
   * @return the size of the KdTree
   */
  def size: Int

  /**
   *
   * @param range     a range
   * @return      the points contained in the range
   */
  def rangeQuery(range: Range): List[(Point2, T)]

  /**
   *
   * @return
   */
  def nodes: List[(Point2, T)]

  /**
   * Pretty printer
   * @param indentation
   * @return
   */
  def pprint(indentation: Int): String

//  def map[A, B](f: A => B): KdTree

}

/**
 * Nil
 */
class Nil[T] extends KdTree[T] {

  val size = 0

  override def rangeQuery(range: Range): List[(Point2, T)] = List()

  override def nodes: List[(Point2, T)]  = List()

  override def toString = "Nil"

  override def pprint(ind: Int): String = ""

}

object Nil extends Nil[Nothing]

/**
 * Leaf
 * @param p
 */
case class Leaf[T](val p: Point2, val value: T) extends KdTree[T] {

  val size = 1

  override def rangeQuery(range: Range): List[(Point2, T)]
    = if (range.inRange(p)) List((p, value)) else List()

  override def nodes : List[(Point2, T)]
    = List((p, value))

  override def toString
    = s"Leaf($p, $value)"

  override def pprint(ind: Int): String
    = " " * ind + toString

}

/**
 *
 * @param dimension
 * @param median
 * @param ls
 * @param es
 * @param hs
 */
case class Node[T](val dimension: Int,
                   val median: Double,
                   val ls: KdTree[T] = Nil,
                   val es: KdTree[T] = Nil,
                   val hs: KdTree[T] = Nil) extends KdTree[T] {

  def size = 1 + ls.size + es.size + hs.size

  override def rangeQuery(r: Range): List[(Point2, T)] = {
    r.compareIth(dimension, median) match {
      case (-1,  _) => hs.rangeQuery(r)
      case ( 0,  _) => es.rangeQuery(r) ++ hs.rangeQuery(r)
      case ( 1, -1) => ls.rangeQuery(r) ++ es.rangeQuery(r) ++ hs.rangeQuery(r)
      case ( 1,  0) => ls.rangeQuery(r) ++ es.rangeQuery(r)
      case ( 1,  1) => ls.rangeQuery(r)
    }
  }

  override def nodes: List[(Point2, T)] = {
    // ls.nodes ++ es.nodes ++ hs.nodes
    val rs = ListBuffer[(Point2, T)]()
    rs ++= ls.nodes
    rs ++= es.nodes
    rs ++= hs.nodes
    rs.result()
  }

  override def toString = s"Node($dimension, $median, $ls, $es, $hs)"

  override def pprint(ind: Int): String = {
    val indent = " " * ind
    val ls2 = ls.pprint(ind+2)
    val es2 = es.pprint(ind+2)
    val hs2 = hs.pprint(ind+2)
    s"$indent$dimension, $median\nl:$ls2\ne:$es2\nh:$hs2"
  }

}

/**
 * KdTree companion
 */
object KdTree {

  import Order.divideByMedian2

  def build[T](d: Int, xs: List[(Point2, T)]): KdTree[T] =
    xs match {
      case List() => Nil
      case List(x) => new Leaf[T](x._1, x._2)
      case _ => {
        val j: Int = (d + 1) % 2
        val p = divideByMedian2[(Point2, T)](p => p._1.ith(d))(xs)
        new Node(d, p.median._1.ith(d), build(j, p.ls), build(j, p.es), build(j, p.hs))
      }
    }

  def fromList[T](xs: List[(Point2, T)]): KdTree[T] = {
    xs match {
      case List() => Nil
      case _ => build(0, xs)
    }
  }

}