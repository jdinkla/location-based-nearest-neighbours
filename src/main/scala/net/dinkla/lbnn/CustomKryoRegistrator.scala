package net.dinkla.lbnn

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.serializers.FieldSerializer
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

    //val ciSer = new FieldSerializer(kryo, classOf[CheckIn])
    //ciSer.getField("date").setClass(classOf[DateTime], dtSer)
    //kryo.register(classOf[CheckIn], ciSer)

    kryo.register(classOf[CheckIn])
    kryo.register(classOf[CI])
    kryo.register(classOf[CIO])

  }

}