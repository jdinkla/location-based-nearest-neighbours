package net.dinkla.lbnn.utils

trait Utilities {

  def mkdir(dir: String): Unit

  def deldir(dir: String): Unit

  def exists(file: String): Boolean

  def merge(src: String, dst: String): Unit

  def download(url: String,  dest: String): Unit

}



