package net.dinkla.lbnn.utils

import java.io.FileInputStream
import java.util.Properties

/**
 * Created by Dinkla on 23.06.2015.
 */
class Parameters(val config: String) {

  lazy val properties = {
    val properties = new Properties()
    val in = new FileInputStream(config)
    //val in = getClass.getResourceAsStream(config)
    properties.load(in)
    in.close()
    properties
  }

  def get(key: String): String = properties.getProperty(key)

  def getOrDefault(key: String, default: String): String = properties.getOrDefault(key, default).toString

}
