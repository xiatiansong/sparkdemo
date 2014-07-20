package com.sunshine.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.Seconds
import org.apache.spark.storage.StorageLevel


/**
 * Chen Chao
 */
object NetworkWordCount {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: NetworkWordCount <hostname> <port> <seconds>\n" +
        "In local mode, <master> should be 'local[n]' with n > 1")
      System.exit(1)
    }

    StreamingExamples.setStreamingLogLevels()

    // 新建StreamingContext
    //val ssc = new StreamingContext(args(0), "NetworkWordCount", Seconds(args(3).toInt),
    //System.getenv("SPARK_HOME"), StreamingContext.jarOfClass(this.getClass))

    val conf = new SparkConf
    conf.setMaster("local[2]")
    conf.setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(args(2).toInt))


    val lines = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_ONLY_SER)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

