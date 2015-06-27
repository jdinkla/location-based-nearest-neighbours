package net.dinkla.lbnn.utils

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 27.06.2015.
 */
class CSVSuite extends FunSuite {

  test("toString") {
    val csv = new CSV("key", "value")
    csv.add("a", 1)
    csv.add("b", 2)
    assert(csv.toString() == "key;value\r\na;1\r\nb;2\r\n")
  }

  test("toString with non default crlf and sep") {
    val csv = new CSV(List("key", "value"), crlf="\r", sep=",")
    csv.add("a", 1)
    csv.add("b", 2)
    assert(csv.toString() == "key,value\ra,1\rb,2\r")
  }

}
