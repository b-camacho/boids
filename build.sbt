enablePlugins(ScalaJSPlugin)
enablePlugins(JSDependenciesPlugin)
scalaVersion := "2.12.10" // or any other Scala version >= 2.10.2
libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.8",
  "org.singlespaced" %%% "scalajs-d3" % "0.3.4"
)
name := "Boids"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

skip in packageJSDependencies := false
//persistLauncher := true
//jsDependencies +=
//  "org.webjars" % "jquery" % "2.2.1" / "jquery.js" minified "jquery.min.js"
jsDependencies += "org.webjars" % "d3js" % "3.5.17" / "3.5.17/d3.min.js"
