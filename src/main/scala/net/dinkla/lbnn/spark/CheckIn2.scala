package net.dinkla.lbnn.spark

import java.text.SimpleDateFormat

import CheckIn._

/**
 * Created by Dinkla on 23.06.2015.
 */
class CheckIn2(val id: CustomerId,
          val date: Long,
          val locX: Coordinate,
          val locY : Coordinate,
          val locId: LocationId)
  extends java.io.Serializable
  with Ordered[CheckIn2] {

  override def compare(that: CheckIn2): Int = {
    val a = id compare that.id
    if (a != 0) a else (date compare that.date)
  }

  override def toString = s"$id, $date, $locX, $locY, $locId"
}

object CheckIn2 {

  val format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")

  def parse(parts: Array[String]) : CheckIn2 = {
    require(parts.size == 5)
    val id = parts(0).toInt
    val date = format.parse(parts(1)).getTime()
    val locX = parts(2).toDouble
    val locY = parts(3).toDouble
    val locId = parts(4).toInt
    //    new CheckIn(id, date, locX, locY, locId)
    new CheckIn2(id, date, locX, locY, locId)
  }

}
