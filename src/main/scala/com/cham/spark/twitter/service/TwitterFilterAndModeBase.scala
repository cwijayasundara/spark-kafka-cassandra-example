package com.cham.spark.twitter.service

import java.io.File

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by cwijayasundara on 25/11/2016.
  */
trait TwitterFilterAndModeBase {

  val twitterBaseDirPath:String = SparkResourceSetUp.getTwitterBaseDir
  val twitterModelDirPath: String = SparkResourceSetUp.getTwitterModelDirPath

  val tweetDirectory: File = new File(twitterBaseDirPath)
  val modelDirectory: File = new File(twitterModelDirPath)
  val numClusters: Int = 10
  val numIterations: Int = 100

}
