package net.dinkla.lbnn.spark

import net.dinkla.lbnn.utils.{CSV, TextDate, Parameters}
import org.apache.spark.rdd.RDD

/**
 *
 */
object ETLFunctions extends Serializable {

  /**
   * Create YYYYMMDD, YYYYMM, YYYY, HH
   * @param dt
   * @return
   */
  def createTimePairs(dt: TextDate): List[(String, Int)] = {
    val ds = List(dt.getDate, dt.getYYYYMM, dt.getYear, dt.getHour)
    ds map { x => (x, 1) }
  }

  /**
   * Filter an rdd and print it to console.
   *
   * @param p
   * @param rdd
   * @tparam T
   * @return
   */
  def debugFilterPrint[T](p: T => Boolean)(rdd: RDD[T]) = {
    val rdd2 = rdd.filter(p)
    println(rdd2.collect().mkString("\n"))
  }

  /**
   * Create a text representation of pair RDD consisting of two columns.
   *
   * @param column1
   * @param column2
   * @param rdd
   * @tparam K
   * @tparam V
   * @return
   */
  def mkCSV[K, V](column1: String, column2: String, rdd: RDD[(K, V)]): String = {
    val csv = new CSV(column1, column2)
    rdd.collect().foreach { x => csv.add(x._1, x._2) }
    csv.toString
  }

  /**
   * Create a text representation of pair <code>Seq</code> consisting of two columns.
   * @param column1
   * @param column2
   * @param seq
   * @tparam K
   * @tparam V
   * @return
   */
  def mkCSV[K, V](column1: String, column2: String, seq: Seq[(K, V)]): String = {
    val csv = new CSV(column1, column2)
    seq.foreach { x => csv.add(x._1, x._2) }
    csv.toString
  }

  /**
   * Returns the path for a property or a default.
   *
   * @param props
   * @param dir
   * @param key
   * @param default
   * @return
   */
  def getPath(props: Parameters, dir: String, key: String, default: String)
    = dir + "/" + props.getOrDefault(key, default)

}
