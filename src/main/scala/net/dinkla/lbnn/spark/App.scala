package net.dinkla.lbnn.spark

import net.dinkla.lbnn.utils.Utilities
import org.apache.spark.SparkContext

/**
 * Created by Dinkla on 25.06.2015.
 */
trait App {

  type Command

  def parse(xs: Array[String]): Command

  def run(cmd: Command, sc: SparkContext, utils: Utilities): Unit

}
