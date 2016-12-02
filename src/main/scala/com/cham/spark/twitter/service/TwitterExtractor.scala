package com.cham.spark.twitter.service

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.StreamingContext
import com.google.gson.Gson
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object TwitterExtractor extends App {

  val sc: StreamingContext = SparkResourceSetUp.getStreamingContext
  val twitterPath:String = SparkResourceSetUp.getTwitterBaseDir
  val numberOfTweets: Int = 10000

  TweetCollector.extractTweetsAsJson(sc,numberOfTweets,twitterPath)

}

object TweetCollector{

  // resources for Cassandra intg
  val sparkConfigForCassandraIntg = new SparkConf(true).set("spark.cassandra.connection.host", "127.0.0.1")
    .setMaster("local[4]")
    .setAppName("Weather App")
  val cassandraConnection = CassandraConnector(sparkConfigForCassandraIntg)
  val sparkContextForCassandraIntg = new SparkContext(sparkConfigForCassandraIntg)

  // tweeter extractor
   def extractTweetsAsJson(sc: StreamingContext, numberOfTweets: Int, path:String): Unit ={

    // get the live twitter stream and map to json
    val tweetStream: DStream[String] = TwitterUtils.createStream(sc, None).map(new Gson().toJson(_))

     tweetStream.print()
     var numTweetsCollected = 0L
     tweetStream.foreachRDD { (rdd) =>
       val count = rdd.count
       if (count > 0) {
         rdd.saveAsTextFile(path)
         numTweetsCollected += count
         if (numTweetsCollected > numberOfTweets) System.exit(0)
       }
     }
    sc.start()
    sc.awaitTermination()
  }

}
