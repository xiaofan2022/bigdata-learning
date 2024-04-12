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
    val session: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    val dataFrame: DataFrame = session.sqlContext.read.parquet("hdfs://hadoop101:9000/warehouse/hudi/customers/65508e9b-8ab4-4e3d-ad57-82bfa76fb8ed_0-1-0_20240412212613591.parquet")
    dataFrame.take(100).foreach(print)
    session.stop()
  }

}
