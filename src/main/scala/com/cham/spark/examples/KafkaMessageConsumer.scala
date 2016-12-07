package com.cham.spark.examples

import java.util.{Collections, Properties}

import com.cham.spark.streaming.actor.SparkCassandraKafkaIntg
import com.google.gson.Gson
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

 /*
  *
  * Created by cwijayasundara on 01/12/2016.
  */

object KafkaMessageConsumer extends SparkCassandraKafkaIntg{

  val topics = Array("spark-streaming")

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "group1",
    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  def main(args: Array[String]) {

    consumeFromSparkStreamingApi

  }

  /*
   * Method that use the direct kafka consumer API
   */

  def consumeFromSparkStreamingApi(): Unit ={

    val stream = KafkaUtils.createDirectStream[String, String](
      sc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(new Gson().toJson(_)).print()

    sc.start()
    sc.awaitTermination()
  }

  // not working
  def consumeFromDirectKafkaApi(): Unit ={

    val kafkaProps: Properties = new Properties
    kafkaProps.put("bootstrap.servers", "localhost:9092")
    kafkaProps.put("group.id", "poc")
    kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    val kafkaConsumer = new KafkaConsumer[String, String](kafkaProps)
    kafkaConsumer.subscribe(Collections.singletonList("spark-streaming"))
    val numberOfMessages = kafkaConsumer.poll(100).count()
    println("Number of messages in the Kafka topic is " + numberOfMessages)
  }
}
