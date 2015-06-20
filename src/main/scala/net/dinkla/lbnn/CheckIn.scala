package net.dinkla.lbnn

import java.text.SimpleDateFormat
import java.util.Date

import net.dinkla.lbnn.CheckIn.{LocationId, Coordinate, CustomerId}
import org.joda.time.DateTime

trait C[T] {

  def parse(parts: Array[String]) : T

}

class CIO(val id: CustomerId,
          val date: Long,
          val locX: Coordinate,
          val locY : Coordinate,
          val locId: LocationId)
  extends java.io.Serializable
  with Ordered[CIO] {

  override def compare(that: CIO): Int = {
    val a = id compare that.id
    if (a != 0) a else (date compare that.date)
  }

  override def toString = s"$id, $date, $locX, $locY, $locId"
}

object CIO {

  val format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")

  def parse(parts: Array[String]) : CIO = {
    require(parts.size == 5)
    val id = parts(0).toInt
    val date = format.parse(parts(1)).getTime()
    val locX = parts(2).toDouble
    val locY = parts(3).toDouble
    val locId = parts(4).toInt
    //    new CheckIn(id, date, locX, locY, locId)
    new CIO(id, date, locX, locY, locId)
  }

}

class CI(val id: CustomerId,
         val date: DateTime,
         val locX: Coordinate,
         val locY : Coordinate,
         val locId: LocationId)
  extends java.io.Serializable
  with Ordered[CheckIn]
{

  override def compare(that: CheckIn): Int = {
    val a = id compare that.id
    if (a != 0) {
      return a
    } else {
      return date.compareTo(that.date)
    }
  }

  override def toString = s"$id, $date, $locX, $locY, $locId"
}

object CI {

  val format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")

  def parse(parts: Array[String]) : CI = {
    require(parts.size == 5)
    val id = parts(0).toInt
    val date = new DateTime(format.parse(parts(1)))
    val locX = parts(2).toDouble
    val locY = parts(3).toDouble
    val locId = parts(4).toInt
    //    new CheckIn(id, date, locX, locY, locId)
    new CI(id, date, locX, locY, locId)
  }

}

/**
 * Created by dinkla on 19/06/15.
 */
class CheckIn(val id: CustomerId,
              val date: DateTime,
              val locX: Coordinate,
              val locY : Coordinate,
              val locId: LocationId)
  extends java.io.Serializable
  with Ordered[CheckIn]
{

  override def compare(that: CheckIn): Int = {
    val a = id compare that.id
    if (a != 0) {
      return a
    } else {
      return date.compareTo(that.date)
    }
  }

  override def toString = s"$id, $date, ($locX, $locY), $locId"

}

object CheckIn {

  type CustomerId = Int
  type Coordinate = Double
  type LocationId = Int

  val format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")

  def split(line: String): Array[String] = line.split("[ \t]+")

  def parse(parts: Array[String]) : CheckIn = {
    require(parts.size == 5)
    val id = parts(0).toInt
    val date = new DateTime(format.parse(parts(1)))
    val locX = parts(2).toDouble
    val locY = parts(3).toDouble
    val locId = parts(4).toInt
    new CheckIn(id, date, locX, locY, locId)
  }

}
