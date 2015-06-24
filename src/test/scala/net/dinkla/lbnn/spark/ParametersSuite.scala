package net.dinkla.lbnn.spark

import org.scalatest.FunSuite

/**
 * Created by Dinkla on 23.06.2015.
 */
class ParametersSuite extends FunSuite {

  test("parameters") {
    val ps = new Parameters("/local.properties")
    assert(ps.workDir == "temp")
  }

}
