package net.dinkla.lbnn

import org.scalatest.FunSuite

/**
 * Created by dinkla on 20/06/15.
 */
class RangeSuite extends FunSuite{

  test("compareIth") {
    def r = new Rectangle(Point2(0, 10), Point2(10, 20))
    assert(r.compareIth(0, -1) == (-1, -1))
    assert(r.compareIth(0,  0) == ( 0, -1))
    assert(r.compareIth(0,  1) == ( 1, -1))
    assert(r.compareIth(0,  9) == ( 1, -1))
    assert(r.compareIth(0, 10) == ( 1,  0))
    assert(r.compareIth(0, 11) == ( 1,  1))

    assert(r.compareIth(1,  9) == (-1, -1))
    assert(r.compareIth(1, 10) == ( 0, -1))
    assert(r.compareIth(1, 11) == ( 1, -1))
    assert(r.compareIth(1, 19) == ( 1, -1))
    assert(r.compareIth(1, 20) == ( 1,  0))
    assert(r.compareIth(1, 21) == ( 1,  1))
  }
}
