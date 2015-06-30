#!/bin/bash

spark-submit    --class net.dinkla.lbnn.Main --master spark://v1:7077 --deploy-mode client target/scala-2.10/lbnn-assembly-1.0.jar cluster.properties $1 $2 $3
spark-submit.sh --class net.dinkla.lbnn.Main --master spark://v1:7077 --conf "spark.executor.extraJavaOptions=-Djava.library.path=/home/ubuntu/hadoop/lib/native" --deploy-mode client target/scala-2.10/lbnn-assembly-1.0.jar cluster.properties %1 %2 %3