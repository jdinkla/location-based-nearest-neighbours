name := "lbnn"                          // "location-based-nearest-neighbours"
version := "1.0"
scalaVersion := "2.10.5"                // needed for hadoop

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0" % "provided",
  "org.apache.hadoop" % "hadoop-common" % "2.6.0" % "provided",

  "org.apache.hadoop" % "hadoop-client" % "2.6.0" % "provided",
  "org.apache.hadoop" % "hadoop-mapreduce-client-app" % "2.6.0" % "provided",
  "org.apache.hadoop" % "hadoop-mapreduce-client-common" % "2.6.0" % "provided",
  "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.6.0" % "provided",
  "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "2.6.0" % "provided",
  "org.apache.hadoop" % "hadoop-mapreduce-client-shuffle" % "2.6.0" % "provided",

  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.apache.spark" %% "spark-core" % "1.4.0" % "provided",
  "com.esotericsoftware" % "kryo" % "3.0.2"
)

scalacOptions += "-feature"

mainClass in (Compile, run) := Some("net.dinkla.lbnn.Main")
