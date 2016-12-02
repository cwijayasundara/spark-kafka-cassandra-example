package com.cham.spark.twitter.service

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import java.util.Properties
import java.util.Collections

import com.google.gson.Gson
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Consumes messages off Kafka streams
  *
  *
  *
  * Created by cwijayasundara on 01/12/2016.
  */

object KafkaMessageConsumer {

    def main(args: Array[String]) {

      consumeFromSparkStreamingApi

    }

  /*
   * Method that use the direct kafka consumer API
   */

  def consumeFromSparkStreamingApi(): Unit ={

    val sparkConf = new SparkConf().setAppName("spark-consumer").setMaster("local[4]")
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "group1",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("poc")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.map(new Gson().toJson(_)).print()

    ssc.start()
    ssc.awaitTermination()
  }

  // not working
  def consumeFromDirectKafkaApi(): Unit ={

    val kafkaProps: Properties = new Properties
    kafkaProps.put("bootstrap.servers", "localhost:9092")
    kafkaProps.put("group.id", "poc")
    kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    val kafkaConsumer = new KafkaConsumer[String, String](kafkaProps)
    kafkaConsumer.subscribe(Collections.singletonList("poc"))
    val numberOfMessages = kafkaConsumer.poll(100).count()
    println("Number of messages in the Kafka topic is " + numberOfMessages)
  }
}
