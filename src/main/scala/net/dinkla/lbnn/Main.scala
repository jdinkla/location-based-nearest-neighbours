package net.dinkla.lbnn

import java.util
import java.util.Date

import com.esotericsoftware.kryo.Kryo
import net.dinkla.lbnn.preprocess.Preprocess
import net.dinkla.lbnn.spark.{CIO, CheckIn}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.KryoRegistrator

/**
 * Created by dinkla on 19/06/15.
 */
object Main {

  val testRun: Boolean = true

  val destDir = "temp"
  //  val url = "https://snap.stanford.edu/data/loc-gowalla_totalCheckins.txt.gz"
  val url = "http://mathforum.org/workshops/sum96/data.collections/datalibrary/Eur.pop.XL.zip.xls"
  val dest = destDir + "/" + "loc-gowalla_totalCheckins.txt.gz"

  val destTest = destDir + "/" + "loc-gowalla_totalCheckins_small.txt.gz"

  val tmpInputSortedDir = destDir + "/" + "temp-sorted"

  val tmpOutputDir = destDir + "/" + "temp-out"


  def download(): Unit = {
    Utilities.download(url, dest)
  }

  def getSparkContext(): SparkContext = {
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("net.dinkla.lbnn")
      .set("spark.executor.memory", "3g")
      .set("spark.kryo.registrator", "net.dinkla.lbnn.spark.CustomKryoRegistrator")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    new SparkContext(conf)
  }

  def createSorted[T](sc: SparkContext): RDD[CheckIn] = {
    val input: RDD[String] = sc.textFile(if (testRun) destTest else dest)

    val input2 = input.map(CheckIn.split)
    //    objs.take(10).foreach(x => println(List(x: _*)))
    val objs = input2.map(CheckIn.parse)

    //objs.take(10).foreach(println)
    val objs2 = objs.sortBy(c => c, true)
    objs2
  }

  def main(args: Array[String]) {

    val download = false
    if (download) {
      Utilities.mkdir(destDir)
      //download()
    }

    val sc : SparkContext = getSparkContext()

    val save: Boolean = false

    // create
    if (save) {
      Utilities.deldir(tmpInputSortedDir)
      val input: RDD[String] = sc.textFile(if (testRun) destTest else dest)
      val input2 = input.map(CheckIn.split)
//      val objs = input2.map(CI.parse)
      //val objs = input2.map(CheckIn.parse)
      val objs = input2.map(CIO.parse)
      val objs2 = objs
      objs2.saveAsObjectFile(tmpInputSortedDir)
    } else {
      // load
      Utilities.deldir(tmpOutputDir)
//      val rdd = sc.objectFile[CI](tmpInputSortedDir)
      //val rdd = sc.objectFile[CheckIn](tmpInputSortedDir)
      val rdd = sc.objectFile[CIO](tmpInputSortedDir)
      rdd.take(10).map(println)
    }

    //    objs2.take(10).foreach(println)

    //val pairs = objs.map( ci => (ci.id, ci)).sortBy()

    //input2.saveAsObjectFile(tmpOutputDir);
    //input2.saveAsTextFile(tmpOutputDir);

  }
}
