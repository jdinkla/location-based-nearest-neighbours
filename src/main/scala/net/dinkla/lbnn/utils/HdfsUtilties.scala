package net.dinkla.lbnn.utils

import java.net.{URL, URI}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataOutputStream, Path, FileSystem, FileUtil}

/**
 * Created by Dinkla on 25.06.2015.
 */
class HdfsUtilties(val hc: Configuration) extends Utilities {

  def mkdir(dir: String): Unit = {
    val dirFS = FileSystem.get(new URI(dir), hc)
    dirFS.mkdirs(new Path(dir))
  }

  def deldir(dir: String): Unit = {
    val dirFS = FileSystem.get(new URI(dir), hc)
    dirFS.delete(new Path(dir), true)
  }

  def exists(path: String): Boolean = {
    val dirFS = FileSystem.get(new URI(path), hc)
    dirFS.exists(new Path(path))
  }

  // http://deploymentzone.com/2015/01/30/spark-and-merged-csv-files/
  def merge(src: String, dst: String): Unit = {
    val srcFS = FileSystem.get(new URI(src), hc)
    val dstFS = FileSystem.get(new URI(dst), hc)
    dstFS.delete(new Path(dst), true)
    FileUtil.copyMerge(srcFS,  new Path(src), dstFS,  new Path(dst), true, hc, null)
  }

  // TODO does not work yet
  // TODO buffered version writing directly into HDFS needed
  def download(urlStr: String,  dest: String): Unit = {
    val local: String = "hdfsutilities.download.dat"   // TODO smell
    val url = new URL(urlStr)

    import scala.language.postfixOps
    import java.io.File
    import sys.process._
    url #> new File(local) !!

    val destFS = FileSystem.get(new URI(dest), hc)
    destFS.copyFromLocalFile(new Path(local), new Path(dest))
  }

}
