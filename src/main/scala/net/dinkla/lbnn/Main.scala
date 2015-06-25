package net.dinkla.lbnn

import net.dinkla.lbnn.utils.{Utilities, Parameters, LocalUtilities, HdfsUtilties}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

import scala.IllegalArgumentException

/**
 * Created by dinkla on 19/06/15.
 */
object Main {

  import net.dinkla.lbnn.spark._

  def main(args: Array[String]) {

    // get the parameter file
    require(args.size > 0)
    val paramFile = args(0)
    val props = new Parameters(paramFile)

    // TODO dynamic lookup of class
    // get the app class
//    val className = props.get("app.class")
//    val clz = Class.forName(className)
//    require(clz != null)
    val app = new CheckInApp(props)
    val cmd = if (args.size > 1) app.parse(args.tail)
              else throw new IllegalArgumentException("command needed")
    val sc = SparkUtils.getSparkContext(props)

    // filesystem
    // TODO dynamic lookup of class
    val filesystem = props.getOrDefault("filesystem", "local")
    val utils: Utilities = filesystem match {
      case "local" => new LocalUtilities(sc.hadoopConfiguration)
      case "hdfs" => new HdfsUtilties(sc.hadoopConfiguration)
    }
    require(utils != null)

    // run
    app.run(cmd, sc, utils)
  }

}
