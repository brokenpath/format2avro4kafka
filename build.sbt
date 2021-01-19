ThisBuild / scalaVersion     := "2.12.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"
avroStringType := "String"
enablePlugins(JmhPlugin)

lazy val root = (project in file("."))
  .settings(
    name := "csv2avro2kafka",
    resolvers +=  "Linkedin bintray" at "https://dl.bintray.com/linkedin/maven",
    libraryDependencies ++= Seq(
      "org.apache.avro" % "avro" % "1.9.2",
      "com.linkedin.avroutil1" % "helper-all" % "0.2.22",
      "com.linkedin.avroutil1" % "avro-fastserde" % "0.2.22",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.11.4",
      "org.slf4j" % "slf4j-simple" % "1.7.30",
      "org.xerial.snappy" % "snappy-java" % "1.1.7.7"
    )
  )



addCommandAlias(
  "runProfiler",
  "jmh:run -i 3 -wi 2 -f 1 -t 1  -prof async:output=flamegraph"
)