package net.dinkla.lbnn.utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils

/**
 * Replacement for `org.apache.hadoop.fs.FileUtil.copyMerge`, which was deprecated in
 * Hadoop 2.7 and removed in Hadoop 3.0.
 *
 * Merges all files in `srcDir` (ordered by file name, so Spark's `part-00000`, `part-00001`,
 * ... keep their order) into a single `dstFile`, optionally deleting the source directory
 * afterwards. The merge is a plain byte-concatenation, which is valid for the gzip part files
 * produced by `saveAsTextFile(..., GzipCodec)`: concatenated gzip members form a valid gzip
 * stream.
 */
object HadoopIO {

  def copyMerge(srcFS: FileSystem, srcDir: Path,
                dstFS: FileSystem, dstFile: Path,
                deleteSource: Boolean, conf: Configuration): Boolean = {
    if (!srcFS.getFileStatus(srcDir).isDirectory) return false
    val out = dstFS.create(dstFile, true)
    try {
      srcFS.listStatus(srcDir)
        .filter(_.isFile)
        .sortBy(_.getPath.getName)
        .foreach { status =>
          val in = srcFS.open(status.getPath)
          try IOUtils.copyBytes(in, out, conf, false)
          finally in.close()
        }
    } finally {
      out.close()
    }
    if (deleteSource) srcFS.delete(srcDir, true) else true
  }

}
