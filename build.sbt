enablePlugins(ScalaJSPlugin)
enablePlugins(JSDependenciesPlugin)
scalaVersion := "2.12.10" // or any other Scala version >= 2.10.2
libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.8",
  "org.singlespaced" %%% "scalajs-d3" % "0.3.4"
)
name := "Boids"

scalaJSUseMainModuleInitializer := true

skip in packageJSDependencies := false

