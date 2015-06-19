package net.dinkla.lbnn

import java.io.File

/**
 * Created by dinkla on 19/06/15.
 */
object Utilities {

  def mkdir(dir: String): Unit = {
    new File(dir).mkdirs()
  }

  def download(url: String,  dest: String): Unit = {
    import sys.process._
    import java.net.URL
    import java.io.File
    new URL(url) #> new File(dest) !!
  }

}
