package com.cham.spark.twitter.service

import java.io.File

import com.google.gson.{Gson, GsonBuilder, JsonParser}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SQLContext}

/**
  * Created by cwijayasundara on 25/11/2016.
  */
object TwitterFilterAndModel extends App with TwitterFilterAndModeBase{

  val sc: SparkContext = SparkResourceSetUp.getSparkContext
  val sqlc: SQLContext = SparkResourceSetUp.getSqlContext

  new ExamineAndTrainModel(sc,sqlc,tweetDirectory,modelDirectory,numClusters,numIterations).markAndTrain()

}

import TwitterClassifier.featurize
import SparkResourceSetUp.spark.implicits._

class ExamineAndTrainModel(sc:SparkContext, sqlc: SQLContext,twitterDir:File, modelDir: File, numCluster: Int, numIter:Int){

  def markAndTrain(): Unit ={
    //load the tweets from the disk
    val tweets: RDD[String] = sc.textFile(twitterDir.getCanonicalPath)
    // print a sample of tweets
    val gson: Gson = new GsonBuilder().setPrettyPrinting().create
    val jsonParser = new JsonParser

    val tweetTable = sqlc
                        .read
                        .json(twitterDir.getCanonicalPath)
                        .cache()
    tweetTable.createOrReplaceTempView("tweetTable")

    println("------Tweet table Schema---")
    tweetTable.printSchema()

    println("------Sample Lang, Name, text---")
    sqlc
      .sql("SELECT user.lang, user.name, text FROM tweetTable LIMIT 1000")
      .collect
      .foreach(println)

    println("------Total count by languages Lang, count(*)---")
    sqlc
      .sql("SELECT user.lang, COUNT(*) as cnt FROM tweetTable GROUP BY user.lang ORDER BY cnt DESC limit 1000")
      .collect
      .foreach(println)

    println("------Training the model ---")
    val texts: Dataset[String] = sqlc
                                    .sql("SELECT text from tweetTable")
                                    .map(_.toString)

    // Cache the vectors RDD since it will be used for all the KMeans iterations.

    val vectors = texts.rdd
                       .map(featurize)
                       .cache()

    vectors.count()  // Calls an action on the RDD to populate the vectors cache.
    val model: KMeansModel = KMeans.train(vectors, numCluster, numIter)

    sc.makeRDD(model.clusterCenters, numCluster)
      .saveAsObjectFile(modelDir.getCanonicalPath)

    println("----100 example tweets from each cluster")
    0 until numCluster foreach { i =>
      println(s"\nCLUSTER $i:")
      texts.take(100) foreach { t =>
        if (model.predict(featurize(t)) == i) println(t)
      }
    }

  }
}
