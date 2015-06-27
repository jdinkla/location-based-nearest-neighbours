rem call in client mode
spark-submit.cmd --class net.dinkla.lbnn.Main --master spark://v1:7077 --conf "spark.executor.extraJavaOptions=-Djava.library.path=/home/ubuntu/hadoop/lib/native" --deploy-mode client target/scala-2.10/lbnn-assembly-1.0.jar cluster.properties %1 %2 %3
