package com.cham.spark.twitter.service

import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{StreamingContext}
import com.google.gson.Gson


object TwitterExtractor extends App {

  val sc: StreamingContext = SparkResourceSetUp.getStreamingContext
  val twitterPath:String = SparkResourceSetUp.getTwitterBaseDir
  val numberOfTweets: Int = 100000

  TweetCollector.extractTweetsAsJson(sc,numberOfTweets,twitterPath)

}

object TweetCollector{

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

  // TODO
  def persistTweetsToCassandra(): Unit = {

  }

}
