package net.dinkla.lbnn

import java.text.SimpleDateFormat
import net.dinkla.lbnn.kd.KdTree
import net.dinkla.lbnn.spark.CheckIn
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import org.joda.time.{LocalDate, DateTime}

/**
 * Created by dinkla on 19/06/15.
 */
object Main {

  def getSparkContextLocal(): SparkContext = {
    val conf = new SparkConf()
      .setMaster("local[*]")    // TODO Master and #cores [*]
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

  def main(args: Array[String]) {

    import net.dinkla.lbnn.spark._

//    val cmd: Command = new Download()
    val cmd: Command = new CreateSample(1000)
//    val cmd: Command = new SortByUser
//    val cmd: Command = new SortByTime
//    val cmd: Command = new StatsGlobal
//    val cmd: Command = new StatsTime
//    val cmd: Command = new StatsUser
//    val cmd: Command = new StatsGeo
//    val cmd: Command = new Tmp
//    val cmd: Command = new FindUser(10971)
//    val cmd: Command = PointInTime("20100904000000")

    val local = true

    if (local) {
      val sc = getSparkContextLocal()
      val app = new CheckInApp("/local.properties", sc)
      app.run(cmd)
    } else {
      val sc = getSparkContextRemote()
      val app = new CheckInApp("/cluster.properties", sc)
      app.run(cmd)
    }

  }
}
