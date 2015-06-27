package net.dinkla.lbnn.utils

import scala.collection.mutable.ListBuffer

/**
 * Created by Dinkla on 27.06.2015.
 */
class CSV(val headers: Seq[String], val crlf: String = "\r\n", val sep: String =";") {

  val numColumns = headers.size

  val ls = new ListBuffer[List[Any]]()

  def this(ss: String*) {
    this(List(ss: _*))
  }

  def add(xs: Any*) = {
    require(xs.size == numColumns)
    val rs = new ListBuffer[Any]()
    for (x <- xs) rs += x
    ls += rs.result()
  }

  override def toString: String = {
    val rs = new StringBuilder()
    rs ++= headers.mkString("", sep, crlf)
    for (l <- ls) {
      val row: List[String] = l.map { x => x.toString() }
      val line = row.mkString("", sep, crlf)
      rs ++= line
    }
    rs.result()
  }

}
