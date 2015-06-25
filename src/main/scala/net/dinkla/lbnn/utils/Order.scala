package net.dinkla.lbnn.utils

import scala.collection.mutable.ListBuffer

/**
 * Created by dinkla on 20/06/15.
 */

class Order {

}

/**
 * 
 */
object Order {

  /**
   * Partition
   * @param median
   * @param ls
   * @param es
   * @param hs
   * @tparam T
   */
  class Partition[T](val median: T,
                     val ls: List[T],
                     val es: List[T],
                     val hs: List[T]) {

    override def toString: String = s"($median, $ls, $es, $hs)"

    def map[S](f: T => S): Partition[S]
      = new Partition(f(median), ls map f, es map f, hs map f)

  }

  /**
   * TODO not efficient O(n log n), possible in O(n), but difficult to implement faster
   * @param xs
   * @tparam T
   * @return
   */
  def median[T <% Ordered[T]](xs: Seq[T]) = {
    val n = xs.size
    val n2 = n / 2
    xs.sorted.toList(n2)
  }

  /**
   * TODO not efficient O(n log n), possible in O(n), but difficult to implement faster
   * @param f
   * @param xs
   * @tparam T
   * @return
   */
  def median2[T](f: T => Double)(xs: Seq[T]) = {
    val n = xs.size
    val n2 = n / 2
    xs.sortBy(f).toList(n2)
  }

  /**
   *
   * @param elem
   * @param xs
   * @tparam T
   * @return
   */
  def partition3[T <% Ordered[T]](elem: T, xs: Seq[T]): Partition[T] = {
    val ls = ListBuffer[T]()
    val es = ListBuffer[T]()
    val hs = ListBuffer[T]()
    for (x <- xs) {
      val buf: ListBuffer[T] =
        if (x == elem) es
        else if (x < elem) ls
        else hs
      buf += x
    }
    new Partition(elem, ls.result(), es.result(), hs.result())
  }

  /**
   *
   * @param f
   * @param elem
   * @param xs
   * @tparam T
   * @return
   */
  def partition32[T](f: T => Double)(elem: T, xs: Seq[T]): Partition[T] = {
    val ls = ListBuffer[T]()
    val es = ListBuffer[T]()
    val hs = ListBuffer[T]()
    val fm = f(elem)
    for (x <- xs) {
      val fx = f(x)
      val buf: ListBuffer[T] =
        if (fx == fm) es
        else if (fx < fm) ls
        else hs
      buf += x
    }
    new Partition(elem, ls.result(), es.result(), hs.result())
  }

  def divideByMedian[T <% Ordered[T]](xs: Seq[T]): Partition[T] = {
    partition3(median(xs), xs)
  }

  def divideByMedian2[T](f: T => Double)(xs: Seq[T]): Partition[T] = {
    partition32(f)(median2(f)(xs), xs)
  }

  /**
   * to test the recursive behaviour
   * @param xs
   * @tparam T
   * @return
   */
  def rec[T <% Ordered[T]](xs: Seq[T]): List[List[T]] = {
    xs match {
      case List() => List()
      case List(x) => List(List(x))
      case _ => {
        val p = divideByMedian[T](xs)
        checkPartitions(xs, p)
        List(rec(p.ls), rec(p.es), rec(p.hs)).flatten
      }
    }
  }

  def checkPartitions[T <% Ordered[T]](xs: Seq[T], p: Partition[T]): Unit = {
    //println(s"p=${p.ls.size}, ${p.es.size}, ${p.hs.size}")
    if (!(p.ls.size < xs.size && p.es.size < xs.size && p.hs.size < xs.size)) {
      assert(p.ls.size < xs.size && p.es.size < xs.size && p.hs.size < xs.size)
    }
  }
}
