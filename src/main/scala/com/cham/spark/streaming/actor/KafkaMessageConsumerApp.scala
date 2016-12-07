package com.cham.spark.streaming.actor

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.ActorContext
import akka.util.Timeout

/**
  * Created by cwijayasundara on 06/12/2016.
  */
object KafkaMessageConsumerApp extends App{

  implicit val system = ActorSystem()
  val kafkaMessageConsumerActor = system.actorOf(KafkaConsumerActorBuilder.props(Timeout.zero), KafkaConsumerActorBuilder.name)
  kafkaMessageConsumerActor ! "consume"

}
