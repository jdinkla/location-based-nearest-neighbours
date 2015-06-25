package net.dinkla.lbnn.spark

import net.dinkla.lbnn.utils.TextDate
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
    //println(s"parts=${parts.foreach(println)}")
    val c = CheckIn.parse(parts)
    assert(c.id == 0)
    assert(c.date.value == "20101012235803")
    assert(c.locX == 30.261599404)
    assert(c.locY == -97.7585805953)
    assert(c.locId == 15372)
  }

  test("testOrdering") {
    val dt1 = new TextDate("20111111")
    val dt2 = new TextDate("20121212")
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
