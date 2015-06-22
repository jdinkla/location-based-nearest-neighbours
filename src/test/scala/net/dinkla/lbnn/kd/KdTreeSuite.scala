package net.dinkla.lbnn.kd

import net.dinkla.lbnn.{Rectangle, Point2, Order}
import Order._
import org.scalatest.FunSuite

import KdTree._

import scalaz.Show
import net.dinkla.lbnn.TestUtils.{ps1, ps2}

/**
 * Created by dinkla on 19/06/15.
 */
class KdTreeSuite extends FunSuite {

  test("fromList empty") {
    val kdt = fromList(List())
    assert(kdt == Nil)
  }

  test("fromList singleton") {
    val p = Point2(0, 0)
    val kdt = fromList(List(p).zipWithIndex)
    assert(kdt match { case Leaf(p,v) => true; case _ => false })
    assert(kdt.size == 1)
  }

  test("fromList many #1") {
    val kdt = fromList(ps1.zipWithIndex)
    assert(kdt.size >= ps1.size)
//    println(kdt.pprint(0))
  }

  test("fromList many #2") {
    val kdt: KdTree[Int] = fromList(ps2.zipWithIndex)
    assert(kdt.size >= ps2.size)
    println(kdt.pprint(0))
  }

  test("rangeQuery #1") {
    val kdt = fromList(ps1.zipWithIndex)
    val r = new Rectangle(Point2(2, 0), Point2(6,5))
    val rs = kdt.rangeQuery(r)
    println(rs)
  }

  test("rangeQuery #2") {
    val kdt = fromList(ps2.zipWithIndex)

    val r1 = new Rectangle(Point2(0, 0), Point2(100, 100))
    val rs1 = kdt.rangeQuery(r1)
    assert(rs1.size == ps2.size)

    val r2 = new Rectangle(Point2(2, 1.5), Point2(4.5,4))
    val rs2 = kdt.rangeQuery(r2)
    assert(rs2.size == 4)
  }

  test("nodes #1") {
    val kdt: KdTree[Int] = fromList(ps2.zipWithIndex)
    assert(kdt.size >= ps2.size)
    val ls = kdt.nodes.map { x => x._2 + 1 }
    assert(ls == List(1, 5, 10, 8, 4, 6, 9, 2, 7, 3))
  }

}
