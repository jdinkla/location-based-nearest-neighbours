package net.dinkla.lbnn.spark

import com.esotericsoftware.kryo.Kryo
import net.dinkla.lbnn.utils.TextDate
import org.apache.spark.serializer.KryoRegistrator

/**
 * Created by dinkla on 19/06/15.
 */

class CustomKryoRegistrator extends KryoRegistrator {

  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[TextDate])
    kryo.register(classOf[CheckIn])
  }

}