package net.dinkla.lbnn

import net.dinkla.lbnn.utils.{LocalUtilities, HdfsUtilties}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * Created by dinkla on 19/06/15.
 */
object Main {

  def getSparkContextLocal(): SparkContext = {
    val conf = new SparkConf()
      .setMaster("local")    // TODO Master and #cores [*]
      .setAppName("net.dinkla.lbnn")
      .set("spark.executor.memory", "7g")
      .set("spark.kryo.registrator", "net.dinkla.lbnn.spark.CustomKryoRegistrator")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    new SparkContext(conf)
  }

  def getSparkContextRemote(): SparkContext = {
    val conf = new SparkConf()
      .setMaster("spark://v1:7077")
      .setAppName("net.dinkla.lbnn")
      //.set("spark.executor.memory", "7g")
      .set("spark.kryo.registrator", "net.dinkla.lbnn.spark.CustomKryoRegistrator")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    new SparkContext(conf)
  }

  import net.dinkla.lbnn.spark._

  def parse(xs: Array[String]): Command = {
    xs match {
      case Array("download") => new Download()
      case Array("sample", ns) =>new CreateSample(ns.toInt)
      case Array("sort-by-user") => new SortByUser()
      case Array("sort-by-time") => new SortByTime()
      case Array("global") => new StatsGlobal()
      case Array("time") => new StatsTime()
      case Array("user") => new StatsUser()
      case Array("geo") => new StatsGeo()
      case Array("tmp") => new Tmp()
      case Array("find", ns) => new FindUser(ns.toInt)
      case Array("pit", ns) => new PointInTime(ns)
      case _ => NullCommand
    }
  }

  def main(args: Array[String]) {

    val cmd = if (args.size > 0) parse(args) else throw new IllegalArgumentException("command needed")

    println(s"cmd=$cmd")

//    val cmd: Command = new Download()
//    val cmd: Command = new CreateSample(1000)
//    val cmd: Command = new FindUser(10971)
//    val cmd: Command = PointInTime("20100904000000")

    val local = false

    if (local) {
      val sc = getSparkContextLocal()
      val utils = new LocalUtilities(sc.hadoopConfiguration)
      val props = new Parameters("/local.properties")
      val app = new CheckInApp(props, sc, utils)
      app.run(cmd)
    } else {
      val sc = getSparkContextRemote()
      val utils = new HdfsUtilties(sc.hadoopConfiguration)
      val props = new Parameters("/cluster.properties")
      val app = new CheckInApp(props, sc, utils)
      app.run(cmd)
    }

  }
}
