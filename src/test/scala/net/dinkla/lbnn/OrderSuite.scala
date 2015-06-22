package net.dinkla.lbnn

import net.dinkla.lbnn.Order._
import net.dinkla.lbnn.TestUtils.ls1
import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * Created by dinkla on 20/06/15.
 */
class OrderSuite extends FunSuite {

  test("testMedian") {
    val lss = ls1.permutations
    lss.foreach( ls => assert(median(ls) == 5 ))
  }

  test("median2") {
    val ls = List((0.0, 0.0), (1.0, 1.0), (-1.0, -1.0))
    val ps = ls.map(x => Point2(x))
    val mx = median2((p:Point2) => p.ith(0))(ps)
    assert(mx == Point2())

    val my = median2((p:Point2) => p.ith(1))(ps)
    assert(my == Point2())
  }

  test("partition3") {
    val ps = partition3[Double](5, ls1)
    assert(ps.median == 5)
    assert(ps.ls.sorted.toList == List(1, 3))
    assert(ps.es.sorted.toList == List(5))
    assert(ps.hs.sorted.toList == List(7, 9))
  }

  test("partition32 by x") {
    val ls2 = ls1.map { x => Point2(x, (x - 5) * (x - 5)) }
    val ps = partition32((p: Point2) => p.ith(0))(Point2(5, 0), ls2)
    assert(ps.median == Point2(5, 0))
    assert(ps.ls.sorted.toList == List(Point2(1, 16), Point2(3, 4)))
    assert(ps.es.sorted.toList == List(Point2(5, 0)))
    assert(ps.hs.sorted.toList == List(Point2(7, 4), Point2(9, 16)))
  }

  test("partition32 by y") {
    val ls2 = ls1.map { x => Point2(x, (x - 5) * (x - 5)) }
    val ps2 = partition32((p:Point2) => p.ith(1))(Point2(0,4), ls2)
    assert(ps2.median == Point2(0,4))
    assert(ps2.ls.sorted.toList == List(Point2(5, 0)))
    assert(ps2.es.sorted.toList == List(Point2(3, 4), Point2(7, 4)))
    assert(ps2.hs.sorted.toList == List(Point2(1, 16), Point2(9, 16)))
  }

  test("partition3 lowest") {
    val ps = partition3[Double](-1, ls1)
    assert(ps.median == -1)
    assert(ps.ls.sorted.toList == List())
    assert(ps.es.sorted.toList == List())
    assert(ps.hs.sorted.toList == List(1, 3, 5, 7, 9))
  }

  test("partition3 highest") {
    val ps = partition3[Double](11, ls1)
    assert(ps.median == 11)
    assert(ps.ls.sorted.toList == List(1, 3, 5, 7, 9))
    assert(ps.es.sorted.toList == List())
    assert(ps.hs.sorted.toList == List())
  }

  class X
  {
    test("rec #1") {
      val rep = 100
      val n = 1000 * 100
      for (x <- 1 to rep) {
        println(s"x: $x")
        val ls = (1 to n).map { x => Random.nextDouble() }.toList
        val rs = Order.rec(ls)
        println(s"x: $x ${rs.size}")

      }
    }

    test("rec #2") {
      val rep = 100
      val n = 1000 * 1000
      var ls : scala.collection.mutable.Seq[Double] = new Array[Double](n)
      for (x <- 1 to rep) {
        println(s"x: $x")
        (0 until n).foreach { i => ls(i) = Random.nextDouble() }
        val rs = Order.rec(ls)
        println(s"x: $x ${rs.size}")
      }
    }

    test("rec #3") {
      val rep = 1
      val n = 100
      for (x <- 1 to rep) {
        println(s"x: $x")
        val ls = (1 to n).map { x => Random.nextDouble() }.toList
        val rs = Order.rec(ls)
        println(s"x: $x ${rs.size}")
      }
    }
  }


}
