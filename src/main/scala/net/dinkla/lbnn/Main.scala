package net.dinkla.lbnn

import net.dinkla.lbnn.utils.{Parameters, LocalUtilities, HdfsUtilties}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * Created by dinkla on 19/06/15.
 */
object Main {

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
    val local = false

    if (local) {
      val props = new Parameters("/local.properties")
      val conf = new SparkConf()
        .setMaster("local")               // TODO local[*]
        .setAppName("net.dinkla.lbnn")
        .set("spark.executor.memory", "7g")
        .set("spark.kryo.registrator", "net.dinkla.lbnn.spark.CustomKryoRegistrator")
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      val sc = new SparkContext(conf)
      val utils = new LocalUtilities(sc.hadoopConfiguration)
      val app = new CheckInApp(props, sc, utils)
      app.run(cmd)
    } else {
      val props = new Parameters("/cluster.properties")
      val conf = new SparkConf()
        .setMaster("spark://v1:7077")
        .setAppName("net.dinkla.lbnn")
        .set("spark.executor.memory", props.getOrDefault("spark.executor.memory", "2g"))
        .set("spark.kryo.registrator", "net.dinkla.lbnn.spark.CustomKryoRegistrator")
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      val sc = new SparkContext(conf)
      val utils = new HdfsUtilties(sc.hadoopConfiguration)
      val app = new CheckInApp(props, sc, utils)
      app.run(cmd)
    }

  }
}
