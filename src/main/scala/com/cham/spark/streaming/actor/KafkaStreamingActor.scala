package com.cham.spark.streaming.actor

import java.util.Properties
import java.io.File

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.rdd.RDD
import com.cham.spark.twitter.service.SparkResourceSetUp
import com.google.gson.{Gson, GsonBuilder, JsonParser}

/**
  * Created by cwijayasundara on 02/12/2016.
  */
object KafkaStreamingActor {

}

object KafkaStreamPublisherActor extends Actor with ActorLogging{

  val topic = "poc"
  // Kafka broker host:port
  val brokers = "localhost:9092"
  val kafkaStringSerializerClass = "org.apache.kafka.common.serialization.StringSerializer"

  // minimum config to connect to Kafka; you can write your own serializers.
  val kafkaProps = new Properties()
  kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaStringSerializerClass)
  kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,kafkaStringSerializerClass)

  val producer = new KafkaProducer[String, String](kafkaProps)

  val sc = SparkResourceSetUp.getSparkContext
  val sourceDir = new File(SparkResourceSetUp.getTwitterBaseDir)

  /* Extract tweets from the disk and place in Kafka
   *
   */
  def publishMessages: Unit ={
      val tweets: RDD[String] = sc.textFile(sourceDir.getCanonicalPath)

      val gson: Gson = new GsonBuilder().setPrettyPrinting().create
      val jsonParser = new JsonParser

      tweets.take(5) foreach { tweet =>
        println(gson.toJson(jsonParser.parse(tweet)))
        val kafkaTweetMessage = new ProducerRecord[String, String](topic,null, gson.toJson(jsonParser.parse(tweet)))
        try{
          producer.send(kafkaTweetMessage).get()
        }catch{
          case ex: Exception => printf("Error while accesing Kafka")
        }
    }
  }

  def receive : Actor.Receive = {
    case e => // ignore
  }
}
