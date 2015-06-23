package net.dinkla.lbnn.spark

import com.esotericsoftware.kryo.Kryo
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer
import org.apache.spark.serializer.KryoRegistrator
import org.joda.time.DateTime

/**
 * Created by dinkla on 19/06/15.
 */

class CustomKryoRegistrator extends KryoRegistrator {

  override def registerClasses(kryo: Kryo) {

    val dtSer = new JodaDateTimeSerializer()
    kryo.register(classOf[DateTime], dtSer)
    kryo.register(classOf[CheckIn])
//    kryo.register(classOf[CheckIn2])
  }

}