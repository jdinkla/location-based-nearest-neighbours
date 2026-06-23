name := "lbnn"                          // "location-based-nearest-neighbours"
version := "1.0"
scalaVersion := "2.13.16"

val sparkVersion  = "3.5.3"
val hadoopVersion = "3.3.4"             // matches the Hadoop client bundled with Spark 3.5

// Spark and Hadoop are `provided` so that `sbt assembly` produces a slim jar suitable
// for `spark-submit` on a cluster (the cluster supplies Spark/Hadoop). The custom `run`
// task below puts the provided dependencies back on the classpath so that a local
// `sbt run` / `sbt "runMain net.dinkla.lbnn.Main ..."` works without a cluster.
libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-hdfs"   % hadoopVersion % Provided,
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion % Provided,
  "org.apache.spark" %% "spark-core"    % sparkVersion  % Provided,
  "org.scalatest"    %% "scalatest"     % "3.2.19"      % Test
)

scalacOptions += "-feature"

Compile / run / mainClass := Some("net.dinkla.lbnn.Main")

// Spark on JDK 17 needs these JPMS module openings (mirrors Spark's own JavaModuleOptions).
val sparkJava17Options = Seq(
  "--add-opens=java.base/java.lang=ALL-UNNAMED",
  "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
  "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
  "--add-opens=java.base/java.io=ALL-UNNAMED",
  "--add-opens=java.base/java.net=ALL-UNNAMED",
  "--add-opens=java.base/java.nio=ALL-UNNAMED",
  "--add-opens=java.base/java.util=ALL-UNNAMED",
  "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
  "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED",
  "--add-opens=java.base/sun.security.action=ALL-UNNAMED",
  "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED",
  "--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED"
)

// Make `run`/`runMain` include `provided` dependencies for local (non-cluster) execution.
Compile / run := Defaults
  .runTask(Compile / fullClasspath, Compile / run / mainClass, Compile / run / runner)
  .evaluated
Compile / runMain := Defaults
  .runMainTask(Compile / fullClasspath, Compile / run / runner)
  .evaluated

run / fork := true
run / javaOptions ++= sparkJava17Options

Test / fork := true
Test / javaOptions ++= sparkJava17Options

// sbt-assembly: keep service registrations, drop the rest of META-INF to avoid conflicts.
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", _*) => MergeStrategy.concat
  case PathList("META-INF", _*)             => MergeStrategy.discard
  case x =>
    val previous = (assembly / assemblyMergeStrategy).value
    previous(x)
}
