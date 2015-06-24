
spark-submit.cmd --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client target/scala-2.10/lbnn-assembly-1.0.jar %1 %2

rem spark-submit.cmd --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client uber.jar $1 $2 $3

rem spark-submit.cmd --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client target/scala-2.11/lbnn_2.11-1.0.jar --jars jars/joda-time.jar

rem spark-submit.cmd --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client target/scala-2.11/lbnn_2.11-1.0.jar

rem spark-submit.cmd --class org.apache.spark.examples.SparkPi --master spark://v1:7077 --deploy-mode client examples.jar

rem spark-submit.cmd --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client --jars hdfs://v1/jars/joda-time-2.8.1.jar target/scala-2.11/lbnn_2.11-1.0.jar


