import sbt._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "TvShowOrganizer"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.25",
    "org.apache.httpcomponents" % "httpcomponents-client" % "4.2.5",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "org.mockito" % "mockito-all" % "1.9.0",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.1.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
