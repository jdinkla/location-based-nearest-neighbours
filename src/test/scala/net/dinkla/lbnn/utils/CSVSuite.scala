package net.dinkla.lbnn.utils

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 27.06.2015.
 */
class CSVSuite extends FunSuite {

  test("toString") {
    val csv = new CSV("key", "value")
    csv.add("number of lines", 1)
    csv.add("number of users", 2)
    csv.add("minimal datetime", 3)
    csv.add("minimal datetime", 4)
    println(csv.toString())

  }

}
