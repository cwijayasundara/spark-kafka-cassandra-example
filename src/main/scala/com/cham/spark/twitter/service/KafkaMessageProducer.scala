package com.cham.spark.twitter.service

/**
  * Created by cwijayasundara on 01/12/2016.
  * You need to install Apache Kafka (0.10.1.0) and Zookeper 3.4.9
  * first start zookeper : <zookeper_home>/bin/zkServer start; this will start at localhost:2181
  * Then start Kafka : <kafka_home>/bin type kafka-server-start /usr/local/etc/kafka/server.properties --override property=
  * create a Kafka topic : <kafka_home>/bin type kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic poc
  * test the Kafka topic: <kafka_home>/bin type kafka-topics --zookeeper localhost:2181 --describe --topic poc
  * list all the Kafka topics: <kafka_home>/bin type kafka-topics --list --zookeeper localhost:2181
  * put some messages to the created topic: <kafka_home>/bin type kafka-console-producer --broker-list localhost:9092 --topic poc
  *                                                           This is a message from console
  *                                                           This is another message from console
  * Check if the messages are there in the topic : <kafka_home>/bin type kafka-console-consumer --zookeeper localhost:2181 --topic test --from-beginning
  * If you get the below error about unable to locate the leader then
  * edit the <kafka> server.properties and add port = 9092
  *                                            advertised.host.name = localhost
  */

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

// TODO : convert this to an (Akka) Actor (KafkaMessageProducingActor)

  object KafkaMessageProducer extends App {

    val topic = "poc"
    // Kafka broker host:port
    val brokers = "localhost:9092"
    val kafkaStringSerializerClass = "org.apache.kafka.common.serialization.StringSerializer"
    // test messages
    val messageStr1 = "nineth message from the app"
    val messageStr2 = "tenth message from the app"

    // minimum config to connect to Kafka; you can write your own serializers.
    val kafkaProps = new Properties()
    kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaStringSerializerClass)
    kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,kafkaStringSerializerClass)

    val producer = new KafkaProducer[String, String](kafkaProps)

    val kafkaMessage1 = new ProducerRecord[String, String](topic,null, messageStr1)
    val kafkaMessage2 = new ProducerRecord[String,String](topic, null, messageStr2)

    // producer.send().get() will call the Future.get() and is sync ; producer.get() is not waiting for Future returns

    try{
      producer.send(kafkaMessage1).get()
      producer.send(kafkaMessage2).get()
    } catch{
      case ex: Exception => println ("Error occured while accessing Kafka")
    }
  }


