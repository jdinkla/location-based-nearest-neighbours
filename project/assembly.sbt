//resolvers += Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += "scalasbt-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases"

resolvers += "Bintray sbt plugin releases" at "http://dl.bintray.com/sbt/sbt-plugin-releases/"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")
