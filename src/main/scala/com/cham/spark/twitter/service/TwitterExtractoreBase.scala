package com.cham.spark.twitter.service

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

trait TwitterExtractorBase  {

  // define some constants
  val numberOfTweets: Int = 10000

}
