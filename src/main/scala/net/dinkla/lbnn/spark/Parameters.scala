package net.dinkla.lbnn.spark

import java.io.FileInputStream
import java.net.URL
import java.util.Properties

/**
 * Created by Dinkla on 23.06.2015.
 */
class Parameters(val config: String) {

  lazy val properties = {
    val properties = new Properties()
    val in = getClass.getResourceAsStream(config)
    properties.load(in)
    in.close()
    properties
  }

  def get(key: String) = properties.getProperty(key)

  def workDir: String = get("workDir")

  def url: String = get("url")

  def srcFile: String = get("srcFile")

  def tmpOutputDir: String = get("tmpOutputDir")

  def srcSortedByUser: String = get("srcSortedByUser")

  def srcSortedByTime: String = get("srcSortedByTime")

  def srcSmallSample: String = get("srcSmallSample")
}
