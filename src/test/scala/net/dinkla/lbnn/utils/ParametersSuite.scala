package net.dinkla.lbnn.utils

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 23.06.2015.
 */
class ParametersSuite extends FunSuite {

  val ps = new Parameters("local.properties")

  test("constr & get") {
    assert(ps.get("spark.appname") == "net.dinkla.lbnn")
  }

  test("constr & getOrDefault") {
    assert(ps.getOrDefault("spark.appname", "xyz") == "net.dinkla.lbnn")
    assert(ps.getOrDefault("supark.appuname", "xyz") == "xyz")
  }

}
