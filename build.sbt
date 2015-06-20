name := "location-based-nearest-neighbours"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.4.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "joda-time" % "joda-time" % "2.8.1",
  "com.esotericsoftware" % "kryo" % "3.0.2",
  "de.javakaffee" % "kryo-serializers" % "0.30"
)


// libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
// libraryDependencies += "joda-time" % "joda-time" % "2.8.1"


