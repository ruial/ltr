ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

excludeLintKeys in Global ++= Set(idePackagePrefix)

lazy val root = (project in file("."))
  .settings(
    name := "ltr",
    idePackagePrefix := Some("com.briefbytes.ltr"),
    libraryDependencies ++= Seq(
      "org.scalanlp" %% "breeze" % "2.1.0", // alternatives for java: EJML and ojAlgo
      "ml.dmlc" %% "xgboost4j" % "2.0.3",
    )
  )
