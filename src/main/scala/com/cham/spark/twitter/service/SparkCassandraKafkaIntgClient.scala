package com.cham.spark.twitter.service


import com.datastax.spark.connector._
import com.google.gson.{Gson}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils

/**
  * Created by cwijayasundara on 28/11/2016.
  */

object SparkCassandraKafkaIntgClient extends App with SparkCassandraKafkaIntg{

  // get the live twitter stream and map to json
  val tweetStream: DStream[String] = TwitterUtils.createStream(sc, None).map(new Gson().toJson(_))

  readWriteFromCassandra(tweetStream)

  // deligate the call to the Kafka integration service
  KafkaMessageProducer.publishMessagesToKafka(tweetStream)

  /* method to save the stream to Cassandra
   * "CREATE KEYSPACE twitter WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };"
   * create a table in Cassandra "create table twitter.tweet(id text PRIMARY KEY, tweet_text text);"
   */

  def readWriteFromCassandra(tStream:DStream[String]): Unit = {

    tStream.foreachRDD((t) => {
      val id = math.random.toString

      t.take(50) foreach { tweet =>
        val tweetJson = gson.toJson(jsonParser.parse(tweet))
        //println("before sending to Cassandra " + tweetJson)
        val collection = sparkContextForCassandraIntg.makeRDD(Seq((id, tweetJson)))
        collection.saveToCassandra("twitter", "tweet", SomeColumns("id", "tweet_text"))
      }
    })

    // read from Cassandra
    cassandraConnection.withSessionDo { session =>
      val result = session.execute("select * from twitter.tweet").all().toArray
      result foreach println
    }
  }

  sc.start()
  sc.awaitTermination()
  sparkContextForCassandraIntg.stop()

}
