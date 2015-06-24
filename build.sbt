//resolvers += Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
//addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")

name := "lbnn"    // "location-based-nearest-neighbours"
version := "1.0"
//scalaVersion := "2.11.6"
scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % "2.7.0" % "provided",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.apache.spark" %% "spark-core" % "1.4.0" % "provided",
//  "org.scalaz" %% "scalaz-core" % "7.1.3",
//  "joda-time" % "joda-time" % "2.8.1",
  "com.esotericsoftware" % "kryo" % "3.0.2"
  // "de.javakaffee" % "kryo-serializers" % "0.30"
)

scalacOptions += "-feature"

mainClass in (Compile, run) := Some("net.dinkla.lbnn.Main")

//assemblySettings
//test in assembly := {}
//assemblyJarName in assembly := "uber.jar"
//mainClass in assembly := Some("net.dinkla.lbnn.Main")

