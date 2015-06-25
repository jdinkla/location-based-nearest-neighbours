package net.dinkla.lbnn.utils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Wrapper for  YYYYMMDDHHmmSS
 *
 * Created by Dinkla on 24.06.2015.
 */
class TextDate(v: String)
  extends Ordered[TextDate] with Serializable {

  require(v.size == 8 || v.size == 14)

  val value: String = if (v.size == 8) v + "000000" else v

  def this(dt: Date) {
    this(TextDate.convert(dt))
  }

  def substring(beginIndex: Int, endIndex: Int): String = value.substring(beginIndex, endIndex)

  def getYear: String = value.substring(0, 4)

  def getMonth: String = value.substring(4, 6)

  def getYYYYMM: String = value.substring(0, 6)

  def getDate: String = value.substring(0, 8)

  def getTime: String = value.substring(8, 14)

  def getHour: String = value.substring(8, 10)

  def getMinute: String = value.substring(10, 12)

  def getSeconds: String = value.substring(12, 14)

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case that: TextDate => value equals that.value
      case _ => false
    }
  }

  override def hashCode(): Int = value.hashCode()

  override def compare(that: TextDate): Int = value compare that.value

  override def toString: String = value

}

object TextDate {

  val format = new SimpleDateFormat("yyyyMMddHHmmss")

  def convert(dt: Date): String = format.format(dt)

}

