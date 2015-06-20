package net.dinkla.lbnn.kd

import org.scalatest.FunSuite

import Order._

/**
 * Created by dinkla on 20/06/15.
 */
class Order$Test extends FunSuite {

  test("testMedian") {
    val ls = List(1, 3, 5, 7, 9)
    val lss = ls.permutations
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
    val ls = List(1, 3, 5, 7, 9)
    val ps = partition3(5, ls)
    assert(ps.m == 5)
    assert(ps.ls.sorted.toList == List(1, 3))
    assert(ps.es.sorted.toList == List(5))
    assert(ps.hs.sorted.toList == List(7, 9))
  }

  test("partition32 by x") {
    val ls = List(1, 3, 5, 7, 9)
    val ls2 = ls.map { x => Point2(x, (x - 5) * (x - 5)) }
    val ps = partition32((p: Point2) => p.ith(0))(Point2(5, 0), ls2)
    assert(ps.m == Point2(5, 0))
    assert(ps.ls.sorted.toList == List(Point2(1, 16), Point2(3, 4)))
    assert(ps.es.sorted.toList == List(Point2(5, 0)))
    assert(ps.hs.sorted.toList == List(Point2(7, 4), Point2(9, 16)))
  }

  test("partition32 by y") {
    val ls = List(1, 3, 5, 7, 9)
    val ls2 = ls.map { x => Point2(x, (x - 5) * (x - 5)) }
    val ps2 = partition32((p:Point2) => p.ith(1))(Point2(0,4), ls2)
    assert(ps2.m == Point2(0,4))
    assert(ps2.ls.sorted.toList == List(Point2(5, 0)))
    assert(ps2.es.sorted.toList == List(Point2(3, 4), Point2(7, 4)))
    assert(ps2.hs.sorted.toList == List(Point2(1, 16), Point2(9, 16)))
  }

  test("partition3 lowest") {
    val ls = List(1, 3, 5, 7, 9)
    val ps = partition3(-1, ls)
    assert(ps.m == -1)
    assert(ps.ls.sorted.toList == List())
    assert(ps.es.sorted.toList == List())
    assert(ps.hs.sorted.toList == List(1, 3, 5, 7, 9))
  }

  test("partition3 highest") {
    val ls = List(1, 3, 5, 7, 9)
    val ps = partition3(11, ls)
    assert(ps.m == 11)
    assert(ps.ls.sorted.toList == List(1, 3, 5, 7, 9))
    assert(ps.es.sorted.toList == List())
    assert(ps.hs.sorted.toList == List())
  }


}
