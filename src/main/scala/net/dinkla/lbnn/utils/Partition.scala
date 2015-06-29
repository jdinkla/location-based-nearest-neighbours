package net.dinkla.lbnn.utils

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

  def checkSizes(n: Int): Boolean
    = ls.size < n && es.size < n && hs.size < n


}