package net.dinkla.lbnn

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.hadoop.fs.{FileUtil, FileSystem, Path}
import org.apache.spark.SparkContext
import java.net.URI

/**
 * Created by dinkla on 19/06/15.
 */
object Utilities {

  def mkdir(dir: String): Unit = {
    new File(dir).mkdirs()
  }

  def deldir(dir: String): Unit = {
    FileUtils.deleteDirectory(new File(dir));
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
    import sys.process._
    import java.net.URL
    import java.io.File
    new URL(url) #> new File(dest) !!
  }

}
