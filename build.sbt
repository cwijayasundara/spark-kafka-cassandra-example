name := """spark-twitter-kafka-cassandra-example"""

version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val sparkVer = "2.0.1"
  Seq(
    "org.apache.spark"     %% "spark-core"              % sparkVer withSources(),
    "org.apache.spark"     %% "spark-mllib"             % sparkVer withSources(),
    "org.apache.spark"     %% "spark-sql"               % sparkVer withSources(),
    "org.apache.spark"     %% "spark-streaming"         % sparkVer withSources(),
    "org.apache.bahir"     %% "spark-streaming-twitter" % "2.0.0"  withSources(),
    "com.google.code.gson" %  "gson"                    % "2.8.0"  withSources(),
    "org.twitter4j"        %  "twitter4j-core"          % "4.0.5"  withSources(),
    "com.github.acrisci"   %% "commander"               % "0.1.0"  withSources()
  )
}


