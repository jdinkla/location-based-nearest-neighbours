package net.dinkla.lbnn.spark

import net.dinkla.lbnn.utils.Parameters
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by Dinkla on 25.06.2015.
 */
object SparkUtils {

  def getSparkContext(props: Parameters): SparkContext = {
    val conf = new SparkConf()
      .setMaster(props.getOrDefault("spark.master", "local"))
      .setAppName(props.getOrDefault("spark.appname", "net.dinkla.lbnn"))
      .set("spark.executor.memory", props.getOrDefault("spark.executor.memory", "7g"))
      .set("spark.kryo.registrator", "net.dinkla.lbnn.spark.CustomKryoRegistrator")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    new SparkContext(conf)
  }

}
