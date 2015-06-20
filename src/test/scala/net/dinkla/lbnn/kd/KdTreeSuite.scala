package net.dinkla.lbnn.kd

import net.dinkla.lbnn.{Rectangle, Point2, Order}
import Order._
import org.scalatest.FunSuite

import KdTree._

import scalaz.Show
import net.dinkla.lbnn.TestUtils.ps1

/**
 * Created by dinkla on 19/06/15.
 */
class KdTreeSuite extends FunSuite {

  test("testDivide") {

  }

  test("testFromList") {

  }

  test("testBuild") {

  }

  test("fromList empty") {
    val kdt = fromList(List())
    assert(kdt == Nil)
  }

  test("fromList singleton") {
    val p = Point2(0, 0)
    val kdt = fromList(List(p))
    assert(kdt match { case Leaf(p) => true; case _ => false })
    assert(kdt.size == 1)
  }

  test("fromList many") {
    val kdt = fromList(ps1)
    assert(kdt.size >= ps1.size)
    println(kdt)

    val et = EmbedTree.embed(kdt)

    println(et.draw(Show.showFromToString[Point2]))
  }

  test("rangeQuery") {
    val kdt = fromList(ps1)

    val r = new Rectangle(Point2(2, 0), Point2(6,5))

    val rs = kdt.rangeQuery(r)

    println(rs)


  }

}
