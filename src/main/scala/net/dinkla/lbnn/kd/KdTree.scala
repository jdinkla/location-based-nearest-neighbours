package net.dinkla.lbnn.kd

import scala.collection.mutable.ListBuffer

/**
 * Created by dinkla on 19/06/15.
 */

sealed trait KdTree {
  def size: Int

  def rangeQuery(r: Range): List[Point2]
}

object Nil extends KdTree {
  val size = 0

  override def rangeQuery(r: Range): List[Point2] = List()

  override def toString = "Nil"

}

case class Leaf(val value: Point2) extends KdTree {
  val size = 1

  override def rangeQuery(r: Range): List[Point2]
    = if (r.inRange(value)) List(value) else List()

  override def toString = s"Leaf($value)"

}

case class Node(val d: Int,
              val med: Double,
              val ls: KdTree = Nil,
              val es: KdTree = Nil,
              val hs: KdTree = Nil) extends KdTree {

  def size = 1 + ls.size + es.size + hs.size

  override def rangeQuery(r: Range): List[Point2] = {
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

  def rangeQuery(r: Range): List[KdTree] = {
    val rs = ListBuffer[KdTree]()


    return rs.result()
  }

}