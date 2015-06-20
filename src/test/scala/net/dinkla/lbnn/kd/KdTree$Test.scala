package net.dinkla.lbnn.kd

import net.dinkla.lbnn.kd.Order._
import org.scalatest.FunSuite

import KdTree._

import scalaz.Show

/**
 * Created by dinkla on 19/06/15.
 */
class KdTree$Test extends FunSuite {

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
    val ls = List(1, 3, 5, 7, 9)
    val ls2 = ls.map { x => Point2(x, (x - 5) * (x - 5)) }
    val kdt = fromList(ls2)
    assert(kdt.size >= ls.size)
    println(kdt)

    val et = EmbedTree.embed(kdt)

    println(et.draw(Show.showFromToString[Point2]))
  }

  test("rangeQuery") {
    val ls = List(1, 3, 5, 7, 9)
    val ls2 = ls.map { x => Point2(x, (x - 5) * (x - 5)) }
    val kdt = fromList(ls2)

    val r = new Rectangle(Point2(2, 0), Point2(6,5))

    val rs = kdt.rangeQuery(r)

    println(rs)


  }

}
