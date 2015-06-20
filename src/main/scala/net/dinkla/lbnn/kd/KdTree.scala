package net.dinkla.lbnn.kd

/**
 * Created by dinkla on 19/06/15.
 */

sealed trait KdTree[+T] {
  def size: Int
}

object Nil extends KdTree[Nothing] {
  val size = 0
}

case class Leaf[T](val value: T) extends KdTree[T] {
  val size = 1
}

case class Node[T](val d: Int,
              val med: T,
              val ls: KdTree[T] = Nil,
              val es: KdTree[T] = Nil,
              val hs: KdTree[T] = Nil) extends KdTree[T] {

  def size = 1 + ls.size + es.size + hs.size

}

object KdTree {

  import Order.divideByMedian

  def build[T <% Ordered[T]](d:Int, xs: List[T]): KdTree[T] = {
    xs match {
      case List() => Nil
      case (x::List()) => new Leaf(x)
      case xs => {
        val j:Int = (d+1) % 2
        val p = divideByMedian2((p:Po(xs)
        new Node[T](d, p.m, build(j, p.ls), build(j, p.es), build(j, p.hs))
      }
    }
  }

  def fromList[T <% Ordered[T]](xs: List[T]): KdTree[T] = {
    xs match {
      case List() => Nil
      case (x::xs) => build(0, xs)
    }
  }


}