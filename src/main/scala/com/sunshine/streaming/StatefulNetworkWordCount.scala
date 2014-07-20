package com.sunshine.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.StreamingContext._

/**
 * Chen Chao
 */
object StatefulNetworkWordCount {
  def main(args: Array[String]) {
    if (args.length < 3) {
      System.err.println("Usage: StatefulNetworkWordCount <hostname> <port> <seconds>\n" +
        "In local mode, <master> should be 'local[n]' with n > 1")
      System.exit(1)
    }

    StreamingExamples.setStreamingLogLevels()

    val updateFunc = (values: Seq[Int], state: Option[Int]) => {
      val currentCount = values.foldLeft(0)(_ + _)
      val previousCount = state.getOrElse(0)
      Some(currentCount + previousCount)
    }

    val conf = new SparkConf
    conf.setMaster("local[2]")
    conf.setAppName("StatefulNetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(args(2).toInt))

    //创建StreamingContext
    //val ssc = new StreamingContext(args(0), "StatefulNetworkWordCount",
    //  Seconds(args(3).toInt), System.getenv("SPARK_HOME"), StreamingContext.jarOfClass(this.getClass))
    ssc.checkpoint(".")

    //创建NetworkInputDStream，需要指定ip和端口
    val lines = ssc.socketTextStream(args(0), args(1).toInt)
    val words = lines.flatMap(_.split(" "))
    val wordDstream = words.map(x => (x, 1))

    //使用updateStateByKey来更新状态
    val stateDstream = wordDstream.updateStateByKey[Int](updateFunc)
    stateDstream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
