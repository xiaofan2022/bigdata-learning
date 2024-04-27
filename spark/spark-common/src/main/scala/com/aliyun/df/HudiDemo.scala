package com.aliyun.df

import org.apache.hudi.DataSourceReadOptions.{BEGIN_INSTANTTIME, INCREMENTAL_FORMAT, QUERY_TYPE, QUERY_TYPE_INCREMENTAL_OPT_VAL}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.slf4j.{Logger, LoggerFactory}

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-26 22:26:00
 * @version 1.0
 */
object HudiDemo {

  val NO_PARTITION_PATH = "file:///D://data//warehouse//hudi//hudi_constomer_no_partition";
  private val logger: Logger = LoggerFactory.getLogger(HudiDemo.getClass)

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("readParqut")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    sparkConf.setMaster("local[*]")
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    logger.info("-----------------插入id=1数据-------")
    //hudiInsertNoPartitonData(mockData(spark, Person(1, "小明", 11, "北京-海淀", java.sql.Timestamp.valueOf("2020-06-10 13:05:24"))))
    hudiQueryCDCInc(spark)
    //增量查询->查询大于 _hoodie_commit_time 时间的数据
    hudiQueryByBeginInstantTime(spark)
    //时间旅行查询（Travel Time）查询特定时间数据
    hudiQueryByInstantTime(spark)
    //根据过滤条件查询hudi数据
    hudiCommonQuery(spark)
    spark.stop()
  }

  def hudiQueryCDCInc(spark: SparkSession) = {
    spark.read.option(BEGIN_INSTANTTIME.key(), 0).
      option(QUERY_TYPE.key(), QUERY_TYPE_INCREMENTAL_OPT_VAL).
      option(INCREMENTAL_FORMAT.key(), "cdc").
      format("hudi").load(NO_PARTITION_PATH).show(false)


  }

  def hudiQueryByBeginInstantTime(spark: SparkSession) = {
    spark.read.format("hudi").
      option(QUERY_TYPE.key(), QUERY_TYPE_INCREMENTAL_OPT_VAL).
      option(BEGIN_INSTANTTIME.key(), 0).
      load(NO_PARTITION_PATH).show(false)
  }

  def hudiQueryByInstantTime(spark: SparkSession) = {
    spark.read.format("hudi").
      option("as.of.instant", "2024-04-26 23:05:18").
      load(NO_PARTITION_PATH).show()
  }

  def hudiCommonQuery(spark: SparkSession) = {
    val hudiDF: DataFrame = spark.read.format("hudi").load(NO_PARTITION_PATH)
    hudiDF.createOrReplaceTempView("temp_hudi")
    spark.sql("select * from temp_hudi where id=1").show(false)
  }

  def hudiInsertNoPartitonData(inputDataFrame: DataFrame) = {
    inputDataFrame.write.format("hudi")
      .option("hoodie.table.name", "hudi_constomer")
      .mode(SaveMode.Overwrite)
      .save(NO_PARTITION_PATH)
  }

  def mockData(spark: SparkSession, person: Person) = {
    spark.createDataFrame(List(person))
  }

}

case class Person(id: Long, name: String, age: Int, address: String, eventTime: java.sql.Timestamp)

