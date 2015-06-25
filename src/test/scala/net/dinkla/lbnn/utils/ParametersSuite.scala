package net.dinkla.lbnn.utils

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 23.06.2015.
 */
class ParametersSuite extends FunSuite {

  val ps = new Parameters("local.properties")

  test("constr & get") {
    assert(ps.get("workDir") == "temp")
  }

  test("constr & getOrDefault") {
    assert(ps.getOrDefault("workDir", "xyz") == "temp")
    assert(ps.getOrDefault("WorkDir", "xyz") == "xyz")
  }

}
