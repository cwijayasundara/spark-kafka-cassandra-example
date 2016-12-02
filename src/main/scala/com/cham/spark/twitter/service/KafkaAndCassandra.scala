package com.cham.spark.twitter.service

import java.io.File

import com.datastax.spark.connector.cql._
import org.apache.spark.{SparkConf, SparkContext}
import com.datastax.spark.connector._

/**
  * Created by cwijayasundara on 28/11/2016.
  */
object KafkaAndCassandra extends App {

  val sparkConfigForCassandraIntg = new SparkConf(true).set("spark.cassandra.connection.host", "127.0.0.1")
                                .setMaster("local[4]")
                                .setAppName("Weather App")

  val cassandraConnection = CassandraConnector(sparkConfigForCassandraIntg)

  val sparkContextForCassandraIntg = new SparkContext(sparkConfigForCassandraIntg)

  val collection = sparkContextForCassandraIntg.makeRDD(Seq(("key9", 9), ("key10", 10)))
  collection.saveToCassandra("test", "kv", SomeColumns("key", "value"))

  // read from Cassandra
  cassandraConnection.withSessionDo { session =>
    //val result = session.execute("select * from isd_weather_data.weather_station limit 10").all().toArray
    val result = session.execute("select * from test.kv").all().toArray
    result foreach println
  }

  sparkContextForCassandraIntg.stop()

}
