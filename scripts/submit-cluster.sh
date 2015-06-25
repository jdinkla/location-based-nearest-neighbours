#!/usr/bin/env bash

spark-submit --class org.apache.spark.examples.SparkPi --master spark://v1:7077 --deploy-mode cluster examples.jar

# rem spark-submit.cmd --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client --jars hdfs://v1/jars/joda-time-2.8.1.jar target/scala-2.11/lbnn_2.11-1.0.jar
# spark-submit --class net.dinkla.lbnn.Main --master spark://v1:7077  --deploy-mode client target/scala-2.11/location-based-nearest-neighbours_2.11-1.0.jar
