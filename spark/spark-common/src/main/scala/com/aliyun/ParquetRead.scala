package com.aliyun

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-12 23:22:48
 * @version 1.0
 */
object ParquetRead {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("readParqut").setMaster("local[*]")
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    val dataFrame: DataFrame = spark.sqlContext.read.parquet("file:///D://data//warehouse//hudi//hudi_constomer_no_partition_flink_new//12cab0b8-a6f9-4bd3-b853-9cf3e3f8ad11_1-8-0_20240416170936656.parquet")
    dataFrame.take(100).foreach(print)


    spark.stop()
  }

}
