package net.dinkla.lbnn.kd

import scala.collection.mutable.ListBuffer

/**
 * Created by dinkla on 20/06/15.
 */

class Order {

}

object Order {

  class Partition[T](val m: T,
                     val ls: List[T],
                     val es: List[T],
                     val hs: List[T]) {

    override def toString: String = s"($m, $ls, $es, $hs)"

  }

  // not efficient O(n log n), possible in O(n), but difficult to implement faster
  def median[T <% Ordered[T]](xs: List[T]) = {
    val n = xs.size
    val n2 = n / 2
    xs.sorted.toList(n2)
  }

  // not efficient O(n log n), possible in O(n), but difficult to implement faster
  def median2[T](f: T => Double)(xs: List[T]) = {
    val n = xs.size
    val n2 = n / 2
    xs.sortBy(f).toList(n2)
  }

  def partition3[T <% Ordered[T]](m: T, xs: List[T]) : Partition[T] = {
    val ls = ListBuffer[T]()
    val es = ListBuffer[T]()
    val hs = ListBuffer[T]()
    for (x <- xs) {
      val buf : ListBuffer[T] =
        if (x == m) es
        else if (x < m) ls
        else hs
      buf += x
    }
    new Partition(m, ls.result(), es.result(), hs.result())
  }

  def partition32[T](f: T => Double)(m: T, xs: List[T]) : Partition[T] = {
    val ls = ListBuffer[T]()
    val es = ListBuffer[T]()
    val hs = ListBuffer[T]()
    val fm = f(m)
    for (x <- xs) {
      val fx = f(x)
      val buf : ListBuffer[T] =
        if (fx == fm) es
        else if (fx < fm) ls
        else hs
      buf += x
    }
    new Partition(m, ls.result(), es.result(), hs.result())
  }

  def divideByMedian[T <% Ordered[T]](xs: List[T]): Partition[T] = {
    partition3(median(xs), xs)
  }

  def divideByMedian2[T](f: T => Double)(xs: List[T]): Partition[T] = {
    partition32(f)(median2(f)(xs), xs)
  }

}
