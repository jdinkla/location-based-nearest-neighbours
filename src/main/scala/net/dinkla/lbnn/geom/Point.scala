package net.dinkla.lbnn.geom

/**
 * Created by dinkla on 19/06/15.
 */

trait Point[T] {
  def dimension(): Int
  def ith(i: Int): T
  //type P
  //val origin: P
}
