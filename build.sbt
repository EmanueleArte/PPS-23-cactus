val scala3Version = "3.3.1"

val warts = Warts.allBut(
  Wart.Var,
  Wart.OptionPartial,
  Wart.Throw,
  Wart.SeqApply,
  Wart.Enumeration,
  Wart.Overloading,
  Wart.While
)

lazy val root = project
  .in(file("."))
  .settings(
    name         := "PPS-23-cactus",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    javacOptions ++= Seq("-source", "17"),

    /* Scalatest */
    libraryDependencies += "org.scalactic" %% "scalactic"       % "3.2.18",
    libraryDependencies += "org.scalatest" %% "scalatest"       % "3.2.18" % Test,
    libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test,

    /* ScalaFX */
    libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33",
    libraryDependencies ++= {
      // Determine OS version of JavaFX binaries
      lazy val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux")   => "linux"
        case n if n.startsWith("Mac")     => "mac"
        case n if n.startsWith("Windows") => "win"
        case _                            => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
    },
    fork := true,

    /* Wartremover */
    wartremoverErrors ++= warts,
    wartremoverWarnings ++= warts
  )
  .enablePlugins(AssemblyPlugin, WartRemover)
  .settings(
    assembly / assemblyJarName := "app.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x                             => MergeStrategy.first
    }
  )
