name := "location-based-nearest-neighbours"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % "2.7.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.apache.spark" %% "spark-core" % "1.4.0",
//  "org.scalaz" %% "scalaz-core" % "7.1.3",
  "joda-time" % "joda-time" % "2.8.1",
  "com.esotericsoftware" % "kryo" % "3.0.2",
  "de.javakaffee" % "kryo-serializers" % "0.30"
)

scalacOptions += "-feature"

//initialCommands in console := "import scalaz._, Scalaz._"



