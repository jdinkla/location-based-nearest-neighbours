package net.dinkla.lbnn.geom

/**
 * Created by Dinkla on 23.06.2015.
 */
final class Point2Temporary(val s: Double) extends Point2(0, 0) {

  def *(p: Point2): Point2 = p * s

}
