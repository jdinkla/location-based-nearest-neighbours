#!/bin/bash

# spark-submit --class org.apache.spark.examples.SparkPi --master spark://v1:7077 --deploy-mode client examples.jar

spark-submit --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client target/scalalbnn_2.11-1.0.jar

# rem spark-submit.cmd --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client --jars hdfs://v1/jars/joda-time-2.8.1.jar target/scala-2.11/lbnn_2.11-1.0.jar
#
