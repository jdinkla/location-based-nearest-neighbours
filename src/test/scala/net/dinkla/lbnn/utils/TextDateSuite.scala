package net.dinkla.lbnn.utils

import java.text.SimpleDateFormat
import java.util.Date

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 24.06.2015.
 */
class TextDateSuite extends FunSuite {

  test("constr #1") {
    val d1 = new TextDate("20010203")
    assert(d1.getYear == "2001")
    assert(d1.getMonth == "02")
    assert(d1.getYYYYMM == "200102")
    assert(d1.getDate == "20010203")
    assert(d1.getTime == "000000")
    assert(d1.getHour == "00")
    assert(d1.getMinute == "00")
    assert(d1.getSeconds == "00")
  }

  test("constr #2") {
    val d1 = new TextDate("20010203123456")
    assert(d1.getYear == "2001")
    assert(d1.getMonth == "02")
    assert(d1.getYYYYMM == "200102")
    assert(d1.getDate == "20010203")
    assert(d1.getTime == "123456")
    assert(d1.getHour == "12")
    assert(d1.getMinute == "34")
    assert(d1.getSeconds == "56")
  }

  test("constr #3") {
    val fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dt: Date = fmt.parse("2001-02-03 12:34:56")
    val d1 = new TextDate(dt)
    assert(d1.getYear == "2001")
    assert(d1.getMonth == "02")
    assert(d1.getYYYYMM == "200102")
    assert(d1.getDate == "20010203")
    assert(d1.getTime == "123456")
    assert(d1.getHour == "12")
    assert(d1.getMinute == "34")
    assert(d1.getSeconds == "56")
  }

}
