package net.dinkla.lbnn

import java.util.Date

import net.dinkla.lbnn.spark.CheckIn
import org.joda.time.DateTime
import org.scalatest.FunSuite

/**
 * Created by dinkla on 19/06/15.
 */
class CheckInSuite extends FunSuite {

  val line = "0       2010-10-12T23:58:03Z    30.261599404    -97.7585805953  15372"
  val parts = CheckIn.split(line)

  test("testSplit") {
    assert(parts.size == 5);
  }

  test("testParse") {
    val c = CheckIn.parse(parts)
    assert(c.id == 0)
    assert(c.date.getYear() == 2010)
    assert(c.date.getMonthOfYear() == 10)
    assert(c.date.getDayOfMonth() == 12)
    assert(c.date.getHourOfDay() == 23)
    assert(c.date.getMinuteOfHour() == 58)
    assert(c.date.getSecondOfMinute() == 3)
    assert(c.locX == 30.261599404)
    assert(c.locY == -97.7585805953)
    assert(c.locId == 15372)
  }

  test("testOrdering") {
    val dt1 = new DateTime(1000L*1000)
    val dt2 = new DateTime(1000L*1000*10)
    val ci1 = new CheckIn(0, dt1, 0, 0, 0)
    val ci2 = new CheckIn(0, dt2, 0, 0, 0)
    val ci3 = new CheckIn(1, dt1, 0, 0, 0)
    val ci4 = new CheckIn(1, dt2, 0, 0, 0)

    assert(ci1 < ci2)
    assert(ci1 < ci3)
    assert(ci1 < ci4)
    assert(ci2 < ci3)
    assert(ci2 < ci4)
    assert(ci3 < ci4)

  }

}
