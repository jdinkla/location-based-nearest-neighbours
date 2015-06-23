package net.dinkla.lbnn.utils

import java.io.File
import java.net.URI

import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
import org.apache.spark.SparkContext

/**
 * Created by dinkla on 19/06/15.
 */
object Utilities {

  def mkdir(dir: String): Unit = {
    new File(dir).mkdirs()
  }

  def deldir(dir: String): Unit = {
    FileUtil.fullyDelete(new File(dir))
//    FileUtils.deleteDirectory(new File(dir));
  }

  def exists(file: String): Boolean = {
    new File(file).exists()
  }

  // http://deploymentzone.com/2015/01/30/spark-and-merged-csv-files/
  def merge(sc: SparkContext, src: String, dst: String): Unit = {
    val hc = sc.hadoopConfiguration
    val srcFS = FileSystem.get(new URI(src), hc)
    val dstFS = FileSystem.get(new URI(dst), hc)
    dstFS.delete(new Path(dst), true)
    FileUtil.copyMerge(srcFS,  new Path(src), dstFS,  new Path(dst), true, hc, null)
  }

  import scala.language.postfixOps

  def download(url: String,  dest: String): Unit = {
    import java.io.File
    import java.net.URL

    import sys.process._
    new URL(url) #> new File(dest) !!
  }

}
