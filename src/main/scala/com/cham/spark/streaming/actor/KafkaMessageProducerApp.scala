package com.cham.spark.streaming.actor

import com.google.gson.{Gson}
import org.apache.spark.streaming.{StreamingContext}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import com.cham.spark.twitter.ml.SparkResourceSetUp

/**
  * Created by cwijayasundara on 07/12/2016.
  */
object KafkaMessageProducerApp extends App {

  val sc:StreamingContext = SparkResourceSetUp.getStreamingContext

  // get the live twitter stream and map to json
  val tweetStream: DStream[String] = TwitterUtils.createStream(sc, None)
                                                 .map(new Gson().toJson(_))

  val kafkaMessageProducer = new KafkaMessageProducer(tweetStream)
  kafkaMessageProducer.publishMessagesToKafka

  sc.start()
  sc.awaitTermination()
}
