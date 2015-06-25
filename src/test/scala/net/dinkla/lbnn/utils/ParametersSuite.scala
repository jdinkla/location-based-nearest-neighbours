package net.dinkla.lbnn.utils

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 23.06.2015.
 */
class ParametersSuite extends FunSuite {

  test("constr & get") {
    val ps = new Parameters("/local.properties")
    assert(ps.get("workDir") == "temp")
  }

  test("constr & getOrDefault") {
    val ps = new Parameters("/local.properties")
    assert(ps.getOrDefault("workDir", "xyz") == "temp")
    assert(ps.getOrDefault("WorkDir", "xyz") == "xyz")
  }

}
