package net.dinkla.lbnn.kd

/**
 * Created by dinkla on 19/06/15.
 */

sealed trait KdTree[+T] {

}

object Nil extends KdTree[Nothing]

class Leaf[T](val value: T) extends KdTree[T]

class Node[T](val d: Int,
              val med: T,
              val ls: KdTree[T] = Nil,
              val es: KdTree[T] = Nil,
              val hs: KdTree[T] = Nil) extends KdTree[T] {

}

object KdTree {

  def divide[T](d: Int, xs: List[T]) = {
    (xs(0), List(), List(), List())
  }

  def build[T](d:Int, xs: List[T]): KdTree[T] = {
    xs match {
      case List() => Nil
      case (x::List()) => new Leaf(x)
      case xs => {
        val j:Int = (d+1) % 2
        val (m, ls, es, hs) = divide(d, xs)
        new Node[T](d, m, build(j,ls), build(j,es), build(j,hs))
      }
    }
  }

  def fromList[T](xs: List[T]): KdTree[T] = {
    xs match {
      case List() => Nil
      case (x::xs) => build(0, xs)
    }
  }


}