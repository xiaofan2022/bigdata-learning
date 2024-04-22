package com.aliyun.sql.hudi

import org.apache.spark.SparkConf
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

/**
 * @description ${description}
 * @author twan
 * @date 2024-04-13 15:04:01
 * @version 1.0
 */
object CurdHudi {
  def insertData(spark: SparkSession) = {
    //    spark.sql(
    //      """
    //        |CREATE TABLE hudi_constomer (
    //        |customer_id INT,
    //        |customer_name STRING,
    //        |email string,
    //        |phone string,
    //        |test_time timestamp,
    //        |insert_date timestamp
    //        |)
    //        | using hudi
    //        |TBLPROPERTIES (
    //        | primaryKey  = 'customer_id',
    //        | 'read.utc-timezone'='true',
    //        |  type = 'cow') location  'file:///D://data//warehouse/hudi/hudi_constomer_no_partition/*'
    //        |""".stripMargin)
    spark.sparkContext.setLogLevel("DEBUG")
    val schema = StructType(Seq(
      StructField("customer_id", IntegerType, nullable = false),
      //      StructField("customer_name", StringType, nullable = false),
      //      StructField("email", StringType, nullable = false),
      //      StructField("phone", StringType, nullable = false),
      //      StructField("test_time", TimestampType, nullable = false),
      StructField("insert_date", TimestampType, nullable = false)
    ))

    // 模拟数据
    val data = Seq(
      Row(2,
        //        "Flink", "john@example.com", "123-456-7890",
        //        java.sql.Timestamp.valueOf("2020-06-10 13:05:24"),
        java.sql.Timestamp.valueOf("2024-03-27 23:13:33"))
      // 添加更多模拟数据...
    )
    // 创建 DataFrame
    val df = spark.createDataFrame(spark.sparkContext.parallelize(data), schema)

    //    df.queryExecution.toRdd.foreachPartition((it)=>{
    //      it.foreach(row=>{
    //        print(row)
    //      })
    //
    //    })
    df.createOrReplaceTempView("result_table")
    //    spark.sql(
    //      """
    //        |insert into hudi_constomer  select * from result_table
    //        |""".stripMargin)
    df.write.format("hudi").
      // option(PARTITIONPATH_FIELD_NAME.key(), "customer_id").
      option("hoodie.table.name", "hudi_constomer")
      .mode(SaveMode.Overwrite)
      .save("file:///D://data//warehouse/hudi/hudi_constomer_no_partition_spark")

  }

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("readParqut")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    //.set("spark.sql.session.timeZone","UTC")
    sparkConf.setMaster("local[*]")

    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

    read(spark)
    //insertData(spark)
    spark.stop()
  }

  //    spark.sql(
  //      """
  //        |CREATE TABLE hudi_constomer (
  //        |customer_id INT,
  //        |customer_name STRING,
  //        |email string,
  //        |phone string,
  //        |test_time timestamp,
  //        |insert_date timestamp,
  //        |dt string
  //        |)
  //        | using hudi
  //        | PARTITIONED BY (dt)
  //        |TBLPROPERTIES (
  //        | primaryKey  = 'customer_id',
  //        | 'read.utc-timezone'='true',
  //        |  type = 'cow') location  'file:///D://data//warehouse/hudi/hudi_constomer/*'
  //        |""".stripMargin)
  def read(spark: SparkSession) = {
    spark.sql(
      """
        |CREATE TABLE hudi_constomer (
        |customer_id INT,
        |customer_name STRING,
        |email string,
        |phone string,
        |test_time timestamp,
        |insert_date timestamp
        |)
        | using hudi
        |TBLPROPERTIES (
        | primaryKey  = 'customer_id',
        | 'read.utc-timezone'='true',
        |  type = 'cow') location  'file:///D://data//warehouse/hudi/hudi_constomer_no_partition_flink_new'
        |""".stripMargin)
    spark.sql("select * from hudi_constomer").show()
  }

}
