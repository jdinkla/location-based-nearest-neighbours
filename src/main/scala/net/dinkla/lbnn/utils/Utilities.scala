package net.dinkla.lbnn.utils

trait Utilities {

  def mkdir(dir: String): Unit

  def deldir(dir: String): Unit

  def delete(ps: String*): Unit = for (p <- ps) { deldir(p) }

  def exists(file: String): Boolean

  def merge(src: String, dst: String): Unit

  def download(url: String,  dest: String): Unit

  def write(path: String, contents: String): Unit

}



