package net.dinkla.lbnn.utils

/**
 * Created by dinkla on 11/06/15.
 */
object Timer {

  def timef[A](id: String)(f: => A): A = {
    def now = System.currentTimeMillis
    val start = now
    val result = f
    val end = now
    println(s"Duration $id ${end - start} ms")
    return result
  }

  def timer[A](id: String)(f: => Unit): Unit = {
    def now = System.currentTimeMillis
    val start = now
    f
    val end = now
    println(s"Duration $id ${end - start} ms")
  }

  def timerGc[A](id: String)(f: => Unit): Unit = {
    System.gc()
    timer(id)(f)
    System.gc()
  }

}
