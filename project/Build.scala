import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "TvShowOrganizer"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.25",
    "org.apache.httpcomponents" % "httpcomponents-client" % "4.2.5",
    "org.mindrot" % "jbcrypt" % "0.3m"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
