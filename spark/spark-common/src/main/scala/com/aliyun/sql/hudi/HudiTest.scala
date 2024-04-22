package com.aliyun.sql.hudi

import org.apache.hudi.QuickstartUtils._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConverters._

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-14 22:35:34
 * @version 1.0
 */
object HudiTest {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("readParqut").setMaster("local[*]")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    val tableName: String = "hudi_trips_cow"
    val basePath = "file:///D://data//warehouse/hudi/hudi_trips_cow"
    val dataGen = new DataGenerator

    //准备数据
    val inserts = convertToStringList(dataGen.generateInserts(10))

    //指定两个分区,读取为dataframe格式
    val df = spark.read.json(spark.sparkContext.parallelize(inserts.asScala, 2))
    //    df.write.mode(SaveMode.Overwrite).format("hudi")
    //      .options(getQuickstartWriteConfigs)
    //      .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
    //      .option(RECORDKEY_FIELD_OPT_KEY, "uuid")
    //      .option(PARTITIONPATH_FIELD_OPT_KEY, "partitionpath")
    //      .option(HoodieWriteConfig.TABLE_NAME, tableName).save(basePath)

    //val hudiDF = spark.read.format("org.apache.hudi").load("file:///D:\\data\\warehouse\\hudi\\hudi_constomer_no_partition")
    //    val hudiDF: DataFrame = spark.sql(
    //      """
    //        |CREATE TABLE test_fin_ipr_inmaininfo4_test
    //        | using hudi
    //        | location  'file:///D:\\data\\warehouse\\hudi\\hudi_constomer_no_partition';""".stripMargin)

    //    spark.sql("select * from test_fin_ipr_inmaininfo4_test").show()
    spark.read.format("hudi").load("file:///D:\\data\\warehouse\\hudi\\hudi_constomer_no_partition_flink_new").show()
    spark.stop()

  }

}
