package net.dinkla.lbnn.utils

import net.dinkla.lbnn.spark.SparkUtils
import org.apache.hadoop.conf.Configuration
import org.scalatest.funsuite.AnyFunSuite

/**
 * Created by Dinkla on 25.06.2015.
 */
class HdfsUtilitiesSuite extends AnyFunSuite {

if (false)
  test("download") {
    val props = new Parameters("cluster.properties")

    val sc = SparkUtils.getSparkContext(props)
    val hc = sc.hadoopConfiguration
    val u = new HdfsUtilties(hc)
    u.download("http://dinkla.net/download/JoernDinklasSoftwareKenntnisse.txt", "hdfs://v1/tmp/JoernDinklasSoftwareKenntnisse.txt")
  }

}
