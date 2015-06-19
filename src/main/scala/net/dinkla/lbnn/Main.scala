package net.dinkla.lbnn

import java.util
import java.util.Date

import net.dinkla.lbnn.preprocess.Preprocess
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
 * Created by dinkla on 19/06/15.
 */
object Main {

  val destDir = "temp"
  //  val url = "https://snap.stanford.edu/data/loc-gowalla_totalCheckins.txt.gz"
  val url = "http://mathforum.org/workshops/sum96/data.collections/datalibrary/Eur.pop.XL.zip.xls"
  val dest = destDir + "/" + "loc-gowalla_totalCheckins.txt.gz"

  val tmpOutputDir = destDir + "/" + "temp-out"

  def download(): Unit = {
    Utilities.download(url, dest)
  }

  def getSparkContext(): SparkContext = {
    val conf = new SparkConf().setMaster("local").setAppName("net.dinkla.lbnn")
    val sc = new SparkContext(conf)
    sc
  }

  class CheckIn(val id: Int, val date: Date, val locX: Double, val locY : Double, val locId: Int) {

  }

  def main(args: Array[String]) {

    Utilities.mkdir(destDir)
    Utilities.deldir(tmpOutputDir)

    //download()

    val sc : SparkContext = getSparkContext()
    val input : RDD[String] = sc.textFile(dest)

    val input2 = input.map(CheckIn.split)
    //    objs.take(10).foreach(x => println(List(x: _*)))
    val objs = input2.map(CheckIn.parse)

    //objs.take(10).foreach(println)
    val objs2 = objs.sortBy(c => c, true)
    objs2.take(10).foreach(println)

    //val pairs = objs.map( ci => (ci.id, ci)).sortBy()

    //input2.saveAsObjectFile(tmpOutputDir);
    //input2.saveAsTextFile(tmpOutputDir);

  }

}
